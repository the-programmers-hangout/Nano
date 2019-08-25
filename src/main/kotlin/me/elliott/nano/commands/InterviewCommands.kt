package me.elliott.nano.commands

import me.aberrantfox.kjdautils.api.dsl.CommandSet
import me.aberrantfox.kjdautils.api.dsl.commands
import me.aberrantfox.kjdautils.extensions.jda.sendPrivateMessage
import me.aberrantfox.kjdautils.internal.arguments.OnOffArg
import me.elliott.nano.listeners.wasEmbedSent
import me.elliott.nano.services.*

@CommandSet("Interviewee")
fun interviewCommands(interviewService: InterviewService, embedService: EmbedService) = commands {
    command("Next") {
        requiresGuild = false
        description = "Pulls the next question off the top of the queue."
        execute {
            val question = interviewService.getNextQuestion()
                ?: return@execute it.respond("There are no questions currently in the queue.")

            wasEmbedSent = false
            it.author.sendPrivateMessage(embedService.buildQuestionEmbed(question))
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
