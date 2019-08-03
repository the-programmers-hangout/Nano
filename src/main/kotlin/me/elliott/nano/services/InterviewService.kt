package me.elliott.nano.services

import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.extensions.stdlib.idToUser
import me.elliott.nano.data.Configuration
import me.elliott.nano.extensions.toEmbedBuilder
import me.elliott.nano.util.EmbedUtils
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import java.awt.Color
import java.util.*

data class Interview(
        var intervieweeId: String = "insert-id",
        var answerChannel: String = "insert-id",
        var bio: String = "*Please set a bio.*"
)

data class Question(var event: GuildMessageReceivedEvent,
                    var questionText: String,
                    var reviewed: Boolean,
                    var reviewNotificationid: String = "provide-id"
)

@Service
class InterviewService(var configuration: Configuration) {

    var questionReviewStore = mutableMapOf<String, Question>()
    var questionQueue = PriorityQueue<Question>()

    private var interview = Interview()

    var hasInterviewee = false
    var hasAnswerChannel = false
    var interviewStarted = false

    fun setInterviewee(interviewee: User) {
        interview.intervieweeId = interviewee.id
        hasInterviewee = true
    }

    fun setAnswerChannel(answerChannel: TextChannel) {
        interview.answerChannel = answerChannel.id
        hasAnswerChannel = true
    }

    fun setBio(bio: String) {
        interview.bio = bio
    }

    fun startInterview(guild: Guild) {
        val guildConfiguration = configuration.guildConfigurations.first { it.guildId == guild.id }

        if (hasInterviewee && hasAnswerChannel) {
            interviewStarted = true
            val interviewee = interview.intervieweeId.idToUser(guild.jda)
            val participantChannel = guild.jda.getTextChannelById(guildConfiguration.participantChannelId)

            participantChannel!!.sendMessage(EmbedUtils.buildInterviewStartEmbed(interviewee,
                    participantChannel, interview.bio)).queue()
        }
    }

    fun queueQuestionForReview(question: Question) {
        val reviewChannel = question.event.jda.getTextChannelById(configuration
                .getGuildConfig(question.event.guild.id)!!.reviewChannelId)

        question.event.channel.sendMessage(EmbedUtils.buildQuestionSubmittedEmbed(question.event.author)).queue()

        val reviewNotification = reviewChannel!!.sendMessage(EmbedUtils.buildQuestionReviewEmbed(question))
                .complete()


        question.reviewNotificationid = reviewNotification.id

        reviewNotification.addReaction("\u2705").complete()
        reviewNotification.addReaction("\u274C").complete()

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