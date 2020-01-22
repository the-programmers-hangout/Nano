package me.elliott.nano.commands

import me.aberrantfox.kjdautils.internal.arguments.MessageArg
import me.aberrantfox.kjdautils.internal.arguments.SentenceArg
import net.dv8tion.jda.api.entities.Message
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
    command("EditMessage") {
        requiresGuild = true
        description = "Edits the target message in the channel the command was invoked in."
        execute(MessageArg("Message to edit."), SentenceArg("New message text.")) {
            val message = it.args.component1() as Message
            val newMessageText = it.args.component2() as String
            it.channel.editMessageById(message.id, newMessageText).queue()
        }
    }
}