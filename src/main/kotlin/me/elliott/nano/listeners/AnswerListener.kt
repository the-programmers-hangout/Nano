package me.elliott.nano.listeners

import com.google.common.eventbus.Subscribe
import me.elliott.nano.data.Configuration
import me.elliott.nano.services.*
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.api.events.user.UserTypingEvent

class AnswerListener(private val configuration: Configuration, private val interviewService: InterviewService, private val embedService: EmbedService) {

    @Subscribe
    fun onPrivateMessageReceivedEvent(event: PrivateMessageReceivedEvent) {
        if (event.author.isBot) return
        val interview = interviewService.retrieveInterview() ?: return

        if (event.author.id != interview.intervieweeId ||
                event.message.contentRaw.startsWith(configuration.prefix)) return

        val question = interviewService.getCurrentQuestion() ?: return
        val answerChannel = event.jda.getTextChannelById(interview.answerChannel)!!

        if (!question.sentToAnswerChannel)
            answerChannel.sendMessage(embedService.buildResponseEmbed(event.author, question)).complete().also {
                question.sentToAnswerChannel = true
                it.addReaction("⭐").queue()
            }
        answerChannel.sendMessage("**${event.author.name}:** ${event.message.contentRaw}").queue()
    }

    @Subscribe
    fun onUserTypingEvent(event: UserTypingEvent) {
        if (event.user.isBot) return
        val interview = interviewService.retrieveInterview() ?: return
        if (!interview.sendTyping) return
        if (interview.intervieweeId != event.user.id) return
        val interviewChannel = event.jda.getTextChannelById(interview.answerChannel) ?: return

        interviewChannel.sendTyping().queue()
    }
}