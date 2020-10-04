package me.elliott.nano.listeners

import com.gitlab.kordlib.core.entity.channel.TextChannel
import com.gitlab.kordlib.core.event.message.ReactionAddEvent
import me.elliott.nano.data.Configuration
import me.elliott.nano.services.InterviewService
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.dsl.listeners

fun onGuildMessageReactionAddEvent(interviewService: InterviewService, discord: Discord, configuration: Configuration) = listeners {
    on<ReactionAddEvent> {
        val channel = discord.api.getChannelOf<TextChannel>(channelId)!!
        if (getUser().isBot!!) return@on
        if (!interviewService.interviewInProgress() || channelId.value != configuration.reviewChannelId) return@on

        val isApproved = emoji.name == ""
        interviewService.processReviewEvent(channel, getUser(), messageId.value, isApproved)

    }
}