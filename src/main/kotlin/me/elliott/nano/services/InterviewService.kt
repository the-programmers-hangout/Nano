package me.elliott.nano.services

import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.discord.Discord
import me.elliott.nano.data.Configuration
import me.elliott.nano.extensions.toEmbedBuilder
import me.elliott.nano.util.Constants
import net.dv8tion.jda.api.entities.*

import java.awt.Color
import java.util.concurrent.SynchronousQueue

data class Interview(
        private var intervieweeId: String,
        var answerChannel: String,
        var bio: String = "*Please set a bio.*",
        var sendTyping: Boolean = true
) {
    fun isBeingInterviewed(user: User) = user.id == intervieweeId
}

data class Question(
    val authorId: String,
    val questionText: String,
    var reviewed: Boolean = false,
    var sentToAnswerChannel: Boolean = false,
    var reviewNotificationId: String = "provide-id"
)

@Service
class InterviewService(private val configuration: Configuration,
                       private val discord: Discord,
                       private val embedService: EmbedService,
                       private val loggingService: LoggingService) {

    private var questionReviewStore = mutableMapOf<String, Question>()
    private var questionQueue = SynchronousQueue<Question>()
    private var interview: Interview? = null

    fun retrieveInterview() = interview
    fun interviewInProgress() = interview != null
    fun getCurrentQuestion(): Question? = questionQueue.poll()

    private fun createAnswerChannel(interviewee: User, guild: Guild) =
            guild.getCategoryById(configuration.getGuildConfig(guild.id)!!.categoryId)!!
                    .createTextChannel(interviewee.name).complete().id

    fun startInterview(guild: Guild, interviewee: User, bio: String): InterviewCreationResult {
        var interviewCreationResult: InterviewCreationResult = InterviewCreationResult.Success(Interview(interviewee.id,
                createAnswerChannel(interviewee, guild), bio))

        val guildConfiguration = configuration.guildConfigurations.first { it.guildId == guild.id }
        val participantChannel = guild.jda.getTextChannelById(guildConfiguration.participantChannelId)

        participantChannel!!.sendMessage(EmbedService.buildInterviewStartEmbed(interviewee, bio,
            guildConfiguration.questionPrefix)).complete().also {
            it.channel.pinMessageById(it.id).queue()
        }

        interviewee.openPrivateChannel().queue {
            it.sendMessage(EmbedService.buildInterviewInstructionEmbed(configuration.prefix,
                    guild.jda.selfUser.effectiveAvatarUrl)).queue({
            }, {
                loggingService.directMessagesClosedError(guild, interviewee)
                interviewCreationResult = InterviewCreationResult.Error(Constants.DM_CLOSED_ERROR)
            })
        }
        return interviewCreationResult
    }

    fun stopInterview(): Boolean {
        interview ?: return false

        interview = null
        questionQueue.clear()

        return true
    }

    fun queueQuestionForReview(question: Question, guild: Guild) {
        val reviewChannel = discord.jda.getTextChannelById(configuration
                .getGuildConfig(guild.id)!!.reviewChannelId) ?: return

        reviewChannel.sendMessage(embedService.buildQuestionReviewEmbed(question)).queue {
            question.reviewNotificationId = it.id
            questionReviewStore[question.reviewNotificationId] = question

            it.addReaction("\u2705").queue()
            it.addReaction("\u274C").queue()
        }
    }

    fun processReviewEvent(channel: TextChannel, messageId: String, approved: Boolean) {
        val question = questionReviewStore[messageId] ?: return

        if (question.reviewed) return

        question.reviewed = true
        questionReviewStore[messageId] = question

        if (approved)
            questionQueue.add(question)

        channel.retrieveMessageById(messageId).queue {
            channel.editMessageById(it.id, it.embeds.first().toEmbedBuilder()
                .setColor(if (approved) Color.GREEN else Color.RED).build()).queue()
        }
    }
}

sealed class InterviewCreationResult {
    data class Success(val interview: Interview) : InterviewCreationResult() {
        companion object
    }

    data class Error(val message: String) : InterviewCreationResult() {
        companion object
    }
}
