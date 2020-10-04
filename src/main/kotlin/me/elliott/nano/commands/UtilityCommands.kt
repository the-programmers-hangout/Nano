package me.elliott.nano.commands

import com.gitlab.kordlib.core.behavior.edit
import kotlinx.coroutines.flow.map
import me.jakejmattson.discordkt.api.arguments.EveryArg
import me.jakejmattson.discordkt.api.arguments.MessageArg
import me.jakejmattson.discordkt.api.dsl.commands


fun utilityCommands() = commands("Interview") {
    command("ClearChannel") {
        requiresGuild = true
        description = "Clear all messages from a channel."
        execute {
            channel.messages.map { it.delete() }
        }
    }
    command("EditMessage") {
        requiresGuild = true
        description = "Edits the target message in the channel the command was invoked in."
        execute(MessageArg("Message to edit."), EveryArg("New message text.")) {
            val message = args.first
            val newMessageText = args.second

            channel.getMessage(message.id).edit {
                content = newMessageText
            }
        }
    }
}