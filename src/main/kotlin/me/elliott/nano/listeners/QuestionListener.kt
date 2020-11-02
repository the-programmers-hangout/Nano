package me.elliott.nano.listeners

import com.gitlab.kordlib.core.behavior.channel.createEmbed
import com.gitlab.kordlib.core.entity.channel.TextChannel
import com.gitlab.kordlib.core.event.message.MessageCreateEvent
import me.elliott.nano.data.Configuration
import me.elliott.nano.data.Question
import me.elliott.nano.services.InterviewService
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.dsl.listeners
import java.awt.Color

fun onGuildMessageReceivedEvent(interviewService: InterviewService, discord: Discord, configuration: Configuration) = listeners {
    on<MessageCreateEvent> {
        val author = message.author ?: return@on
        if (author.isBot == true) return@on

        val guild = getGuild() ?: return@on
        if (!interviewService.isInterviewActive()) return@on

        val channel = discord.api.getChannelOf<TextChannel>(message.channelId) ?: return@on
        val guildConfig = configuration.guild ?: return@on

        if (channel.id.longValue != guildConfig.participantChannel) return@on
        val questionPrefix = configuration.questionPrefix

        if (message.content.startsWith(questionPrefix) && message.content.removePrefix(questionPrefix).isNotBlank()) {


            channel.createEmbed {
                title = "Question Submitted"
                color = Color.PINK
                description = "**${author.username}**'s question was successfully submitted for review."
            }

            interviewService.queueQuestionForReview(Question(message.content.removePrefix(questionPrefix), author.id.longValue), guild)
        }

    }
}