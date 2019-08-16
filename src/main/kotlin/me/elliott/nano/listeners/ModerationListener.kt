package me.elliott.nano.listeners

import com.google.common.eventbus.Subscribe
import me.elliott.nano.data.Configuration
import me.elliott.nano.services.InterviewService
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent

class ModerationListener(private val interviewService: InterviewService, private val configuration: Configuration) {
    @Subscribe
    fun onGuildMessageReactionAddEvent(event: GuildMessageReactionAddEvent) {
        val guildConfiguration = configuration.getGuildConfig(event.guild.id)!!

        if (event.user.isBot)
            return

        if (!interviewService.interviewRunning() ||
                event.channel.id != guildConfiguration.reviewChannelId) return

        val isApproved = event.reaction.reactionEmote.name == "✅"
        interviewService.processReviewEvent(event, isApproved)
    }
}