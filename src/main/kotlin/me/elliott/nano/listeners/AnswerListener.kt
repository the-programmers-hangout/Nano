package me.elliott.nano.listeners

import com.google.common.eventbus.Subscribe
import me.elliott.nano.data.Configuration
import me.elliott.nano.services.InterviewService
import me.elliott.nano.util.EmbedUtils
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent

class AnswerListener(private val interviewService: InterviewService, private val configuration: Configuration) {

    @Subscribe
    fun onPrivateMessageReceivedEvent(event: PrivateMessageReceivedEvent) {

        if (event.author.isBot || !interviewService.interviewStarted ||
                event.author.id != interviewService.interview.intervieweeId ||
                event.message.contentRaw.startsWith(configuration.prefix)) return

        val question = interviewService.currentQuestion ?: return
        val answerChannel = event.jda.getTextChannelById(interviewService.interview.answerChannel!!)!!

        if (!question.sentToAnswerChannel)
            answerChannel.sendMessage(EmbedUtils.buildResponseEmbed(event.author, question)).queue {
                question.sentToAnswerChannel = true
                answerChannel.sendMessage("**${event.author.name}:** ${event.message.contentRaw}").queue()
            }
    }
}