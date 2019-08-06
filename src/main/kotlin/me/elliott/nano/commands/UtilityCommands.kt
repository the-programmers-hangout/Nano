package me.elliott.nano.commands

import me.aberrantfox.kjdautils.api.dsl.CommandSet
import me.aberrantfox.kjdautils.api.dsl.commands
import me.aberrantfox.kjdautils.internal.command.arguments.UserArg
import me.elliott.nano.services.InterviewService
import net.dv8tion.jda.api.entities.User

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