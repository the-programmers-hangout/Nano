package me.elliott.nano.services

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.behavior.channel.createEmbed
import com.gitlab.kordlib.core.behavior.createTextChannel
import com.gitlab.kordlib.core.behavior.edit
import com.gitlab.kordlib.core.behavior.getChannelOf
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.User
import com.gitlab.kordlib.core.entity.channel.Category
import com.gitlab.kordlib.core.entity.channel.TextChannel
import com.gitlab.kordlib.kordx.emoji.Emojis
import com.gitlab.kordlib.kordx.emoji.toReaction
import me.elliott.nano.data.Configuration
import me.elliott.nano.util.Constants
import me.elliott.nano.util.notNull
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.annotations.Service
import java.awt.Color
import java.util.ArrayDeque

data class Interview(
        private var intervieweeId: String,
        val answerChannel: String,
        var sendTyping: Boolean = true
) {
    fun isBeingInterviewed(user: User) = user.id.value == intervieweeId
}

data class Question(val authorId: String, val questionText: String)

@Service
class InterviewService(private val configuration: Configuration,
                       private val loggingService: LoggingService,
                       private val discord: Discord) {

    private var questionReviewStore = mutableMapOf<String, Question>()
    private var questionQueue = ArrayDeque<Question>()
    private var interview: Interview? = null
    private var currentQuestion: Question? = null
    private var answerMessageMap = mutableMapOf<String, String>()


    fun retrieveInterview() = interview
    fun interviewInProgress() = interview != null
    fun getCurrentQuestion(): Question? = currentQuestion
    fun getQuestionCount() = questionQueue.size
    fun peekTopFive(): List<Question> = questionQueue.toList().take(5)

    fun addAnswerToMap(privateMessageId: String, answerChannelMessageId: String) {
        answerMessageMap[privateMessageId] = answerChannelMessageId
    }

    fun swapQuestion(questionIndex: Int): String {
        var message = "Please provide a valid question ID."

        questionQueue.elementAtOrNull(questionIndex).notNull {
            questionQueue.remove(it)
            questionQueue.addFirst(it)
            message = "Done."
        }
        return message
    }

    fun getNextQuestion(): Question? {
        currentQuestion = questionQueue.poll()
        return currentQuestion
    }

    suspend fun editAnswerChannelMessage(privateMessageId: String, updatedText: String) {
        val answerChannel = discord.api.getChannelOf<TextChannel>(Snowflake(interview!!.answerChannel)) ?: return
        val answerChannelMessageId = answerMessageMap[privateMessageId] ?: return
        answerChannel.getMessage(Snowflake(answerChannelMessageId)).edit {
            content = updatedText
        }
    }

    suspend fun startInterview(guild: Guild, interviewee: User, bio: String): String {
        val botCategory = discord.api.getChannelOf<Category>(Snowflake(configuration.categoryId))
                ?: return Constants.MISSING_CATEGORY_CONFIG


        val answerChannel = guild.createTextChannel {
            name = interviewee.username
            parentId = botCategory.id
        }

        val participantChannel = discord.api.getChannelOf<TextChannel>(Snowflake(configuration.participantChannelId))
                ?: return Constants.MISSING_PARTICIPANT_CONFIG

        answerMessageMap.clear()

        val interviewStart = participantChannel.createEmbed {
            title = "AMA Started - Please Submit Your Questions Below."
            color = Color.CYAN
            description = bio
            thumbnail {
                url = interviewee.avatar.url
            }

            field {  }

            field {
                val questionPrefix = configuration.questionPrefix
                name = "Please begin your question with the following prefix: $questionPrefix"
                value = "**Example:** $questionPrefix What's one of your favorite technologies?"
            }
        }

        interviewStart.pin()


        val privateChannel = interviewee.getDmChannel().asChannelOrNull()
                ?: return Constants.DM_CLOSED_ERROR.also {
                    loggingService.directMessagesClosedError(guild, interviewee)
                }


        val avatar = discord.api.getSelf().avatar.url

        privateChannel.createEmbed {
            thumbnail {
                url = avatar
            }

            title = "How Do I Answer Questions?"
            description = "When you're ready for the next question, simply " +
                    "type: `${configuration.prefix.plus("next")}`. Whenever a question is displayed, " +
                    "any replies you choose to provide will be sent to the interview answer channel until " +
                    "you request the next question or end the interview."

            field {
                name = "Ending The Interview"
                value = "When you're ready to end the interview, message a moderator and they will end it for you."
                inline = true
            }

            field {
                name = "Turn Typing Events On or Off"
                value = "If you want the bot to send your typing events to the answer channel, type " +
                        "`${configuration.prefix.plus("SendTyping")} on/off` *(**On** by default)*"
                inline = true
            }
            color = Color.MAGENTA
        }

        interview = Interview(interviewee.id.value, answerChannel.name)
        loggingService.interviewStarted(guild, interviewee)

        return "**Success:** ${interviewee.username}'s interview has started!"
    }

    fun stopInterview(): Boolean {
        interview ?: return false

        interview = null
        questionQueue.clear()

        return true
    }

    suspend fun queueQuestionForReview(question: Question, guild: Guild, author: User) {
        val reviewChannel = guild.getChannelOf<TextChannel>(Snowflake(configuration.reviewChannelId))

        loggingService.submittedQuestion(guild, author)

        val reviewEmbed = reviewChannel.createEmbed {
            val questionAuthor = discord.api.getUser(Snowflake(question.authorId))
            val authorName = questionAuthor?.username ?: "Unknown user"

            color = Color.LIGHT_GRAY
            description = question.questionText
            footer {
                text = "Asked by $authorName"
                icon = questionAuthor?.avatar?.url
            }
        }

        questionReviewStore[reviewEmbed.id.value]
        reviewEmbed.addReaction(Emojis.whiteCheckMark.toReaction())
        reviewEmbed.addReaction(Emojis.x.toReaction())

    }

    suspend fun processReviewEvent(channel: TextChannel, moderator: User, messageId: String, approved: Boolean) {
        val question = questionReviewStore[messageId] ?: return

        if (question in questionQueue) return

        val author = discord.api.getUser(Snowflake(question.authorId)) ?: return

        if (approved) {
            loggingService.questionApproved(channel.getGuild(), moderator, author)
            questionQueue.offer(question)
        } else {
            loggingService.questionDenied(channel.getGuild(), moderator, author)

            channel.getMessage(Snowflake(messageId)).edit {
                embed {
                    color = if (approved) Color.GREEN else Color.RED
                }
            }
        }
    }
}