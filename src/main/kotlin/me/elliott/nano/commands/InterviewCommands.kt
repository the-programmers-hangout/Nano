package me.elliott.nano.commands

import me.aberrantfox.kjdautils.api.dsl.CommandSet
import me.aberrantfox.kjdautils.api.dsl.commands
import me.aberrantfox.kjdautils.extensions.jda.sendPrivateMessage
import me.aberrantfox.kjdautils.internal.di.PersistenceService
import me.elliott.nano.data.Configuration
import me.elliott.nano.services.InterviewService
import me.elliott.nano.services.Question
import me.elliott.nano.util.EmbedUtils

@CommandSet("Interviewee")
fun interviewCommannds(interviewService: InterviewService, configuration: Configuration,
                       persistenceService: PersistenceService) = commands {

    command("next") {
        requiresGuild = false
        description = "Pulls the next question off the top of the queue."
        execute {
            val question = interviewService.questionQueue.dequeue()
                    ?: it.respond("There are no questions currently in the queue.").also { return@execute }

            interviewService.currentQuestion = question as Question
            return@execute it.author.sendPrivateMessage(EmbedUtils.buildQuestionEmbed(question))
        }
    }
}
