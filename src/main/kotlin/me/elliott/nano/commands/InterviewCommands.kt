package me.elliott.nano.commands

import me.aberrantfox.kjdautils.api.dsl.CommandSet
import me.aberrantfox.kjdautils.api.dsl.commands
import me.aberrantfox.kjdautils.extensions.jda.sendPrivateMessage
import me.aberrantfox.kjdautils.internal.arguments.OnOffArg
import me.elliott.nano.services.InterviewService
import me.elliott.nano.util.EmbedUtils

@CommandSet("Interviewee")
fun interviewCommands(interviewService: InterviewService) = commands {
    command("Next") {
        requiresGuild = false
        description = "Pulls the next question off the top of the queue."
        execute {
            val question = interviewService.getCurrentQuestion()
                ?: return@execute it.respond("There are no questions currently in the queue.")

            it.author.sendPrivateMessage(EmbedUtils.buildQuestionEmbed(question))
        }
    }

    command("SendTyping") {
        requiresGuild = false
        description = "Enables or disables sending typing events to the answer channel."
        expect(OnOffArg)
        execute {
            val isOn = it.args.component1() as Boolean
            val response = if (isOn) "enabled" else "disabled"
            val interview = interviewService.retrieveInterview() ?: return@execute

            interview.sendTyping = isOn
            it.respond("Sending of typing events is now **$response**")
        }
    }
}
