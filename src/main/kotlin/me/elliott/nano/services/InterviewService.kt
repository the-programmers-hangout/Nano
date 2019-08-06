package me.elliott.nano.services

import me.aberrantfox.kjdautils.api.annotation.Service
import me.elliott.nano.data.Configuration
import me.elliott.nano.extensions.toEmbedBuilder
import me.elliott.nano.util.EmbedUtils
import me.elliott.nano.util.Queue
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import java.awt.Color

data class Interview(
        var intervieweeId: String? = null,
        var answerChannel: String? = null,
        var bio: String = "*Please set a bio.*"
)

data class Question(val event: GuildMessageReceivedEvent,
                    val questionText: String,
                    var reviewed: Boolean = false,
                    var sentToAnswerChannel: Boolean = false,
                    var reviewNotificationId: String = "provide-id"
)

@Service
class InterviewService(val configuration: Configuration) {

    private var questionReviewStore = mutableMapOf<String, Question>()

    var questionQueue = Queue<Question>()
    var currentQuestion: Question? = null
    var interview: Interview? = null

    fun interviewRunning() = interview != null

    private fun createAnswerChannel(interviewee: User, guild: Guild) =
            guild.createTextChannel(interviewee.name).complete().id

    fun createInterview(guild: Guild, interviewee: User, bio: String): Interview {
        val guildConfiguration = configuration.guildConfigurations.first { it.guildId == guild.id }
        val participantChannel = guild.jda.getTextChannelById(guildConfiguration.participantChannelId)

        participantChannel!!.sendMessage(EmbedUtils.buildInterviewStartEmbed(interviewee,
                participantChannel, bio)).queue()

        return Interview(interviewee.id, createAnswerChannel(interviewee, guild), bio)
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
            questionQueue.enqueue(question)

        val message = event.channel.retrieveMessageById(event.messageId).complete()

        event.channel.editMessageById(message.id, message.embeds.first().toEmbedBuilder()
                .setColor(if (approved) Color.GREEN else Color.RED).build()).queue()
    }
}