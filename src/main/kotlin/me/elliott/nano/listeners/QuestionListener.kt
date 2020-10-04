package me.elliott.nano.listeners

import com.gitlab.kordlib.core.behavior.channel.createEmbed
import com.gitlab.kordlib.core.entity.channel.TextChannel
import com.gitlab.kordlib.core.event.message.MessageCreateEvent
import me.elliott.nano.data.Configuration
import me.elliott.nano.services.*
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.dsl.listeners
import java.awt.Color


fun onGuildMessageReceivedEvent(interviewService: InterviewService, discord: Discord, configuration: Configuration) = listeners {
    on<MessageCreateEvent> {
        val author = message.author!!
        val guild = getGuild() ?: return@on
        val channel = discord.api.getChannelOf<TextChannel>(message.id) ?: return@on
        val messageText = message.content

        if (!interviewService.interviewInProgress()) return@on
        if (author.isBot!!) return@on

        if (channel.id.value != configuration.participantChannelId) return@on
        val questionPrefix = configuration.questionPrefix

        if (messageText.startsWith(questionPrefix) && messageText.removePrefix(questionPrefix).isNotBlank()) {
            interviewService.queueQuestionForReview(Question(author.id.value, messageText.removePrefix(questionPrefix)), guild, author)

            channel.createEmbed {
                title = "Question Submitted"
                color = Color.PINK
                description = "**${author.username}**'s question was successfully submitted for review."
            }
        }

    }
}