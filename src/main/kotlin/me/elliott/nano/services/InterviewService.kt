package me.elliott.nano.services

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.behavior.channel.createEmbed
import com.gitlab.kordlib.core.behavior.createTextChannel
import com.gitlab.kordlib.core.behavior.edit
import com.gitlab.kordlib.core.behavior.getChannelOf
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.core.entity.User
import com.gitlab.kordlib.core.entity.channel.Category
import com.gitlab.kordlib.core.entity.channel.TextChannel
import com.gitlab.kordlib.kordx.emoji.Emojis
import com.gitlab.kordlib.kordx.emoji.toReaction
import com.gitlab.kordlib.rest.request.KtorRequestException
import me.elliott.nano.data.Configuration
import me.elliott.nano.data.Interview
import me.elliott.nano.data.Question
import me.elliott.nano.embeds.interviewStarted
import me.elliott.nano.embeds.interviewStartedDM
import me.elliott.nano.embeds.reviewEmbed
import me.elliott.nano.extensions.descriptor
import me.elliott.nano.utilities.Constants
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.annotations.Service
import java.awt.Color


@Service
class InterviewService(private val configuration: Configuration, private val discord: Discord, private val loggerService: LoggerService) {
    fun isInterviewActive(): Boolean {
        if (!configuration.isSetup())
            return false

        return configuration.guild!!.interview != null
    }


    suspend fun startInterview(guild: Guild, interviewee: User, bio: String): String {

        val config = configuration.guild ?: return Constants.CONFIG_NOT_SETUP
        if (config.interview != null) return Constants.INTERVIEW_RUNNING

        val amaCategory = discord.api.getChannelOf<Category>(Snowflake(config.amaCategory))
                ?: return Constants.MISSING_CATEGORY_CONFIG
        val participantChannel = discord.api.getChannelOf<TextChannel>(Snowflake(config.participantChannel))
                ?: return Constants.MISSING_PARTICIPANT_CONFIG


        val privateChannel = interviewee.getDmChannel()

        try {
            privateChannel.createEmbed {
                val avatar = discord.api.getSelf().avatar.url

                interviewStartedDM(avatar, configuration.prefix!!)
            }
        } catch (ex: KtorRequestException) {
            return Constants.DM_CLOSED_ERROR
        }


        val answerChannel = guild.createTextChannel {
            name = interviewee.username
            parentId = amaCategory.id
        }

        val interviewStart = participantChannel.createEmbed { interviewStarted(bio, interviewee, configuration.questionPrefix) }

        interviewStart.pin()

        config.interview = Interview(interviewee.id.longValue, answerChannel.id.longValue)
        configuration.save()

        loggerService.log("**Info ::** Interview with ${interviewee.descriptor()} has started.")

        return "**Success:** ${interviewee.username}'s interview has started!"
    }

    suspend fun stopInterview(): Boolean {
        val guildConfig = configuration.guild ?: return false
        val interview = guildConfig.interview ?: return false
        val interviewee = discord.api.getUser(Snowflake(interview.interviewee)) ?: return false

        loggerService.log("**Info ::** Interview with ${interviewee.descriptor()} has ended.")

        guildConfig.interview = null
        configuration.save()
        return true
    }

    suspend fun queueQuestionForReview(question: Question, guild: Guild) {
        val guildConfig = configuration.guild ?: return
        val reviewChannel = guild.getChannelOf<TextChannel>(Snowflake(guildConfig.reviewChannel))
        val interview = guildConfig.interview ?: return
        val questionAuthor = discord.api.getUser(Snowflake(question.author)) ?: return

        loggerService.log("**Info ::** ${questionAuthor.descriptor()} has submitted a question for review.")

        val reviewEmbed = reviewChannel.createEmbed { reviewEmbed(question.questionText, questionAuthor, Color.LIGHT_GRAY) }


        reviewEmbed.addReaction(Emojis.whiteCheckMark.toReaction())
        reviewEmbed.addReaction(Emojis.x.toReaction())

        interview.questionReview[reviewEmbed.id.longValue] = question
        configuration.save()
    }

    suspend fun processReviewEvent(channel: TextChannel, moderator: User, messageId: Long, approved: Boolean) {

        val guildConfig = configuration.guild ?: return
        val interview = guildConfig.interview ?: return
        val question = interview.questionReview[messageId] ?: return
        val author = discord.api.getUser(Snowflake(question.author)) ?: return

        if (approved) {
            loggerService.log("**Info ::** ${moderator.descriptor()} approved ${author.descriptor()}'s question.")

            interview.questions.add(question)

        } else {
            loggerService.log("**Info ::** ${moderator.mention} denied ${author.descriptor()}'s question.")
        }

        channel.getMessage(Snowflake(messageId)).edit {
            val color = if (approved) Color.GREEN else Color.RED

            embed { reviewEmbed(question.questionText, author, color) }
        }

        interview.questionReview.remove(messageId)
        configuration.save()
    }

    fun getQuestionCount() = configuration.guild?.interview?.questions?.count() ?: 0
    fun peekTopFive(): List<Question> = configuration.guild?.interview?.questions?.take(5) ?: listOf()
    fun getNextQuestion(): Question? = configuration.guild?.interview?.questions?.firstOrNull()

    fun swapQuestion(questionIndex: Int): String {
        var message = "Please provide a valid question ID."

        val questionQueue = configuration.guild?.interview?.questions ?: return message

        val questionAtIndex = questionQueue.elementAtOrNull(questionIndex)

        if (questionAtIndex != null) {
            questionQueue.remove(questionAtIndex)
            questionQueue.add(0, questionAtIndex)
            configuration.save()
            message = "Done."
        }

        return message
    }

    fun pushQuestionBack(): String {
        val question = getNextQuestion() ?: return "The queue is empty."
        val interview = configuration.guild?.interview ?: return "Interview isn't running."

        interview.questions.remove(question)
        interview.questions.add(question)

        configuration.save()

        return "Question pushed to back of queue."
    }
}