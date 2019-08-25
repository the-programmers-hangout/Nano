package me.elliott.nano.listeners

import com.google.common.eventbus.Subscribe
import me.elliott.nano.data.Configuration
import me.elliott.nano.services.*
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.api.events.message.priv.PrivateMessageUpdateEvent
import net.dv8tion.jda.api.events.user.UserTypingEvent

var embedSent = false

class AnswerListener(private val configuration: Configuration, private val interviewService: InterviewService, private val embedService: EmbedService) {

    @Subscribe
    fun onPrivateMessageReceivedEvent(event: PrivateMessageReceivedEvent) {
        val author = event.author
        val messageText = event.message.contentRaw
        val interview = interviewService.retrieveInterview() ?: return

        if (author.isBot) return
        if (!interview.isBeingInterviewed(author)) return
        if (messageText.startsWith(configuration.prefix)) return

        val question = interviewService.getCurrentQuestion() ?: return
        val answerChannel = event.jda.getTextChannelById(interview.answerChannel) ?: return

        if (!embedSent) {
            answerChannel.sendMessage(embedService.buildResponseEmbed(author, question)).queue {
                embedSent = true
                it.addReaction("⭐").queue()
            }
        }

        answerChannel.sendMessage("**${author.name}:** $messageText").queue {
            interviewService.addAnswerToMap(event.messageId, it.id)
        }
    }

    @Subscribe
    fun onPrivateMessageUpdateEvent(event: PrivateMessageUpdateEvent) =
            interviewService.editAnswerChannelMessage(event.messageId,
                    "**${event.author.name}:** ${event.message.contentRaw}", event.jda)


    @Subscribe
    fun onUserTypingEvent(event: UserTypingEvent) {
        if (event.user.isBot) return
        val interview = interviewService.retrieveInterview() ?: return
        if (!interview.sendTyping) return
        if (!interview.isBeingInterviewed(event.user)) return
        val interviewChannel = event.jda.getTextChannelById(interview.answerChannel) ?: return

        interviewChannel.sendTyping().queue()
    }
}