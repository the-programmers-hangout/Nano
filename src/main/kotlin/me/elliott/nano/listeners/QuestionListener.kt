package me.elliott.nano.listeners

import com.google.common.eventbus.Subscribe
import me.elliott.nano.data.Configuration
import me.elliott.nano.services.InterviewService
import me.elliott.nano.services.Question
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

class QuestionListener(private val interviewService: InterviewService, private val configuration: Configuration) {

    @Subscribe
    fun onGuildMessageReceivedEvent(event: GuildMessageReceivedEvent) {
        if (event.author.isBot)
            return

        val guildConfiguration = configuration.getGuildConfig(event.guild.id)!!

        if (!interviewService.interviewRunning() ||
                event.channel.id != guildConfiguration.participantChannelId) return

        if (event.message.contentRaw.startsWith(guildConfiguration.questionPrefix)) {
            interviewService.queueQuestionForReview(Question(event, event.message.contentRaw
                    .removePrefix(guildConfiguration.questionPrefix), reviewed = false), event.guild)
        }
    }
}