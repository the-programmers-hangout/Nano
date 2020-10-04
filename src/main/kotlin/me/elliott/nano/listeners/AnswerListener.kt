package me.elliott.nano.listeners

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.behavior.channel.createEmbed
import com.gitlab.kordlib.core.entity.channel.TextChannel
import com.gitlab.kordlib.core.event.channel.TypingStartEvent
import com.gitlab.kordlib.core.event.message.MessageCreateEvent
import com.gitlab.kordlib.core.event.message.MessageUpdateEvent
import com.gitlab.kordlib.kordx.emoji.Emojis
import com.gitlab.kordlib.kordx.emoji.toReaction
import me.elliott.nano.data.Configuration
import me.elliott.nano.services.*
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.dsl.listeners
import java.awt.Color

var embedSent = false


fun onPrivateMessageReceivedEvent(interviewService: InterviewService, configuration: Configuration, discord: Discord) = listeners {
    on<MessageCreateEvent> {
        if (getGuild() != null) return@on
        if (message.author?.isBot == true) return@on

        val author = message.author!!
        val messageText = message.content
        val interview = interviewService.retrieveInterview() ?: return@on

        if (!interview.isBeingInterviewed(author)) return@on
        if (messageText.startsWith(configuration.prefix)) return@on

        val question = interviewService.getCurrentQuestion() ?: return@on
        val answerChannel = discord.api.getChannelOf<TextChannel>(Snowflake(interview.answerChannel)) ?: return@on

        if (!embedSent) {
            val answerMessage = answerChannel.createEmbed {
                val questionAuthor = discord.api.getUser(Snowflake(question.authorId))
                val authorName = questionAuthor?.username ?: "Unknown User"

                title = "${author.username} is answering $authorName's Question:"
                color = Color.MAGENTA
                description = "**Question:** ${question.questionText}"

                footer {
                    text = "Asked by $authorName"
                    icon = questionAuthor?.avatar?.url
                }
            }

            embedSent = true
            answerMessage.addReaction(Emojis.star.toReaction())
        }


        val responseMessage = answerChannel.createMessage("**${author.username}:** $messageText")
        interviewService.addAnswerToMap(message.id.value, responseMessage.id.value)
    }
}

fun onPrivateMessageUpdateEvent(interviewService: InterviewService, configuration: Configuration, discord: Discord) = listeners {
    on<MessageUpdateEvent> {
        val author = message.asMessage().author ?: return@on

        interviewService.editAnswerChannelMessage(messageId.value, "**${author.username}:** ${message.asMessage().content}")
    }
}


fun onUserTypingEvent(interviewService: InterviewService, discord: Discord) = listeners {
    on<TypingStartEvent> {
        val user = getUser()

        if (user.isBot!!) return@on
        val interview = interviewService.retrieveInterview() ?: return@on
        if (!interview.sendTyping) return@on
        if (!interview.isBeingInterviewed(user)) return@on

        val interviewChannel = discord.api.getChannelOf<TextChannel>(Snowflake(interview.answerChannel)) ?: return@on

        interviewChannel.type()
    }
}