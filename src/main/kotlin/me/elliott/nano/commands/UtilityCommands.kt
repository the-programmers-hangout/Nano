package me.elliott.nano.commands

import me.aberrantfox.kjdautils.api.dsl.command.*

@CommandSet("Interview")
fun utilityCommands() = commands {
    command("ClearChannel") {
        requiresGuild = true
        description = "Clear all messages from a channel."
        execute {
            val messages = it.channel.iterableHistory.complete()
            it.channel.purgeMessages(messages)
        }
    }
}