package me.elliott.nano.listeners

import com.google.common.eventbus.Subscribe
import me.elliott.nano.data.Configuration
import me.elliott.nano.services.InterviewService
import me.elliott.nano.services.Question
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

class QuestionListener(private val interviewService: InterviewService, private val configuration: Configuration) {

    @Subscribe
    fun onGuildMessageReceivedEvent(event: GuildMessageReceivedEvent) {
        val guildConfiguration = configuration.getGuildConfig(event.guild.id)
        val user = event.author

        if (user.isBot || !interviewService.interviewStarted ||
                event.channel.id != guildConfiguration!!.participantChannelId) return

        if (event.message.contentRaw.startsWith("[Q&A]")) {
            interviewService.queueQuestionForReview(Question(event, event.message.contentRaw
                    .removePrefix("[Q&A]"), reviewed = false))
        }
    }
}