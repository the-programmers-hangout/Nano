package me.elliott.nano.listeners

import com.gitlab.kordlib.core.entity.channel.TextChannel
import com.gitlab.kordlib.core.event.message.ReactionAddEvent
import kotlinx.coroutines.runBlocking
import me.elliott.nano.data.Configuration
import me.elliott.nano.services.InterviewService
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.dsl.listeners

fun onGuildMessageReactionAddEvent(interviewService: InterviewService, configuration: Configuration) = listeners {
    on<ReactionAddEvent> {
        val user = getUser()
        if (user.isBot == true) return@on
        val guild = getGuild() ?: return@on
        val guildConfig = configuration.guild ?: return@on
        val interview = guildConfig.interview ?: return@on

        if (!user.asMember(guild.id).roleIds.any { it.longValue == guildConfig.staffRole }) return@on
        if (guildConfig.reviewChannel != channelId.longValue) return@on
        if (!interview.questionReview.containsKey(messageId.longValue)) return@on
        val isApproved = emoji.name == "✅"
        val channel = getChannel() as TextChannel

        @Suppress("BlockingMethodInNonBlockingContext")
        runBlocking {
            interviewService.processReviewEvent(channel, getUser(), messageId.longValue, isApproved)
        }

    }
}