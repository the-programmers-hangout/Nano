package me.elliott.nano.services

import me.aberrantfox.kjdautils.api.annotation.Service
import me.elliott.nano.data.Configuration
import me.elliott.nano.extensions.toEmbedBuilder
import me.elliott.nano.util.Constants
import me.elliott.nano.util.EmbedUtils

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import java.awt.Color
import java.util.concurrent.SynchronousQueue

data class Interview(
        var intervieweeId: String,
        var answerChannel: String,
        var bio: String = "*Please set a bio.*",
        var sendTyping: Boolean = true
)

data class Question(val event: GuildMessageReceivedEvent,
                    val questionText: String,
                    var reviewed: Boolean = false,
                    var sentToAnswerChannel: Boolean = false,
                    var reviewNotificationId: String = "provide-id"
)

@Service
class InterviewService(private val configuration: Configuration, private val loggingService: LoggingService) {

    private var questionReviewStore = mutableMapOf<String, Question>()

    var questionQueue = SynchronousQueue<Question>()
    var currentQuestion: Question? = null
    var interview: Interview? = null


    fun interviewRunning() = interview != null

    private fun createAnswerChannel(interviewee: User, guild: Guild) =
            guild.getCategoryById(configuration.getGuildConfig(guild.id)!!.categoryId)!!
                    .createTextChannel(interviewee.name).complete().id

    fun createInterview(guild: Guild, interviewee: User, bio: String): InterviewCreationResult {

        var interviewCreationResult: InterviewCreationResult = InterviewCreationResult.Success(Interview(interviewee.id,
                createAnswerChannel(interviewee, guild), bio))

        val guildConfiguration = configuration.guildConfigurations.first { it.guildId == guild.id }
        val participantChannel = guild.jda.getTextChannelById(guildConfiguration.participantChannelId)

        participantChannel!!.sendMessage(EmbedUtils.buildInterviewStartEmbed(interviewee,
                participantChannel, bio, guildConfiguration.questionPrefix)).complete().also {
            it.channel.pinMessageById(it.id).queue()
        }

        interviewee.openPrivateChannel().queue {
            it.sendMessage(EmbedUtils.buildInterviewInstructionEmbed(configuration.prefix,
                    guild.jda.selfUser.effectiveAvatarUrl)).queue({
            }, {
                loggingService.directMessagesClosedError(guild, interviewee)
                interviewCreationResult = InterviewCreationResult.Error(Constants.DM_CLOSED_ERROR)
            })
        }
        return interviewCreationResult
    }

    fun queueQuestionForReview(question: Question, guild: Guild) {
        val reviewChannel = question.event.jda.getTextChannelById(configuration
                .getGuildConfig(question.event.guild.id)!!.reviewChannelId)

        question.event.channel.sendMessage(EmbedUtils.buildQuestionSubmittedEmbed(question.event.author)).queue()

        reviewChannel!!.sendMessage(EmbedUtils.buildQuestionReviewEmbed(question)).queue {
            question.reviewNotificationId = it.id
            questionReviewStore[question.reviewNotificationId] = question

            it.addReaction("\u2705").queue()
            it.addReaction("\u274C").queue()
        }
    }

    fun processReviewEvent(event: GuildMessageReactionAddEvent, approved: Boolean) {
        val question = questionReviewStore.getOrElse(event.messageId) {
            return
        }

        if (question.reviewed) return

        question.reviewed = true
        questionReviewStore[event.messageId] = question

        if (approved)
            questionQueue.add(question)

        val message = event.channel.retrieveMessageById(event.messageId).complete()

        event.channel.editMessageById(message.id, message.embeds.first().toEmbedBuilder()
                .setColor(if (approved) Color.GREEN else Color.RED).build()).queue()
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
