package me.elliott.nano.listeners

import com.gitlab.kordlib.common.entity.ChannelType
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.behavior.edit
import com.gitlab.kordlib.core.entity.channel.TextChannel
import com.gitlab.kordlib.core.event.channel.TypingStartEvent
import com.gitlab.kordlib.core.event.message.MessageUpdateEvent
import kotlinx.coroutines.runBlocking
import me.elliott.nano.data.Configuration
import me.elliott.nano.extensions.workingWidth
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.dsl.listeners
import me.jakejmattson.discordkt.api.extensions.sendPrivateMessage

fun onPrivateMessageUpdateEvent(configuration: Configuration, discord: Discord) = listeners {
    on<MessageUpdateEvent> {
        val guildId = new.guildId

        if (guildId != null) {
            return@on
        }

        val author = new.author ?: return@on
        var newContent = new.content ?: return@on
        val guildConfig = configuration.guild ?: return@on
        val interview = guildConfig.interview ?: return@on
        if (interview.interviewee.toString() != author.id) return@on
        val user = discord.api.getUser(Snowflake(author.id)) ?: return@on

        val answerChannel = discord.api.getChannelOf<TextChannel>(Snowflake(interview.answerChannel)) ?: return@on
        val answerChannelMessage = interview.answeredQuestions[messageId.longValue] ?: return@on

        // some kind of coroutines bug
        @Suppress("BlockingMethodInNonBlockingContext")
        runBlocking {
            var tooLong = false
            val messageToEdit = answerChannel.getMessage(Snowflake(answerChannelMessage))
            if (messageToEdit.content.startsWith("**${user.username}:** ")) {
                if (newContent.length > user.workingWidth()) {
                    user.sendPrivateMessage("The edited message content is too long. It was not updated. (${newContent.length}/${user.workingWidth()})")

                    tooLong = true
                }

                newContent = "**${user.username}:** $newContent"
            }

            if (!tooLong) {
                messageToEdit.edit {
                    content = newContent
                }

                user.sendPrivateMessage("Message was updated.")
            }
        }
    }
}

fun onUserTypingEvent(discord: Discord, configuration: Configuration) = listeners {
    on<TypingStartEvent> {

        // stupid coroutines bug again
        @Suppress("BlockingMethodInNonBlockingContext")
        runBlocking {
            val user = getUserOrNull() ?: return@runBlocking
            if (user.isBot == true) return@runBlocking

            val guildConfig = configuration.guild ?: return@runBlocking
            val interview = guildConfig.interview ?: return@runBlocking
            if (!interview.sendTyping) return@runBlocking
            if (interview.interviewee != user.id.longValue) return@runBlocking

            if (channel.asChannel().type != ChannelType.DM) return@runBlocking


            val interviewChannel = discord.api.getChannelOf<TextChannel>(Snowflake(interview.answerChannel))
                    ?: return@runBlocking
            interviewChannel.type()
        }

    }
}