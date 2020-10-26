package me.elliott.nano.commands

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import me.elliott.nano.extensions.requiredPermissionLevel
import me.elliott.nano.services.Permission
import me.jakejmattson.discordkt.api.arguments.ChannelArg
import me.jakejmattson.discordkt.api.dsl.commands

fun utilityCommands() = commands("Utility") {
    guildCommand("ClearChannel") {
        requiredPermissionLevel = Permission.GUILD_OWNER
        description = "Clear all messages from a channel."
        execute(ChannelArg.makeNullableOptional()) {
            val channel = args.first ?: channel

            val messages = channel.getMessagesBefore(message.id).map { it.id }.toList()
            channel.bulkDelete(messages)
            message.delete()
        }
    }
}