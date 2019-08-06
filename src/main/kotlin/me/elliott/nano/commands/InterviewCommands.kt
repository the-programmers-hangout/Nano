package me.elliott.nano.commands

import me.aberrantfox.kjdautils.api.dsl.CommandSet
import me.aberrantfox.kjdautils.api.dsl.commands
import me.aberrantfox.kjdautils.extensions.jda.sendPrivateMessage
import me.elliott.nano.services.InterviewService
import me.elliott.nano.util.EmbedUtils

@CommandSet("Interviewee")
fun interviewCommands(interviewService: InterviewService) = commands {
    command("Next") {
        requiresGuild = false
        description = "Pulls the next question off the top of the queue."
        execute {
            val question = interviewService.questionQueue.dequeue()
                    ?: return@execute it.respond("There are no questions currently in the queue.")

            interviewService.currentQuestion = question
            return@execute it.author.sendPrivateMessage(EmbedUtils.buildQuestionEmbed(question))
        }
    }
}
