package me.elliott.nano.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.kjdautils.internal.command.tryRetrieveSnowflake
import me.elliott.nano.data.Configuration
import me.elliott.nano.services.InterviewService
import me.elliott.nano.util.EmbedUtils
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.api.events.user.UserTypingEvent

class AnswerListener(private val interviewService: InterviewService, private val configuration: Configuration) {

    @Subscribe
    fun onPrivateMessageReceivedEvent(event: PrivateMessageReceivedEvent) {

        if (event.author.isBot) return
        if (!interviewService.interviewRunning()) return

        val interview = interviewService.interview!!

        if (event.author.id != interviewService.interview!!.intervieweeId ||
                event.message.contentRaw.startsWith(configuration.prefix)) return

        val question = interviewService.currentQuestion ?: return
        val answerChannel = event.jda.getTextChannelById(interview.answerChannel)!!

        if (!question.sentToAnswerChannel)
            answerChannel.sendMessage(EmbedUtils.buildResponseEmbed(event.author, question)).complete().also {
                question.sentToAnswerChannel = true
                it.addReaction("⭐").queue()
            }
        answerChannel.sendMessage("**${event.author.name}:** ${event.message.contentRaw}").queue()
    }

    @Subscribe
    fun onUserTypingEvent(event: UserTypingEvent) {
        if (event.user.isBot) return
        if (!interviewService.interviewRunning()) return

        val interview = interviewService.interview!!
        if (!interview.sendTyping) return
        if (interview.intervieweeId != event.user.id) return

        val interviewChannel = tryRetrieveSnowflake(event.jda) {
            it.getTextChannelById(interview.answerChannel)
        } as TextChannel

        interviewChannel.sendTyping().queue()
    }
}