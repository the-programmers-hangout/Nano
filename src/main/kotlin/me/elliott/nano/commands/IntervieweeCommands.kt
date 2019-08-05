package me.elliott.nano.commands

import me.aberrantfox.kjdautils.api.dsl.CommandSet
import me.aberrantfox.kjdautils.api.dsl.commands
import me.aberrantfox.kjdautils.extensions.jda.fullName
import me.aberrantfox.kjdautils.internal.command.Pass
import me.aberrantfox.kjdautils.internal.command.arguments.SentenceArg
import me.aberrantfox.kjdautils.internal.command.arguments.TextChannelArg
import me.aberrantfox.kjdautils.internal.command.arguments.UserArg
import me.aberrantfox.kjdautils.internal.di.PersistenceService
import me.elliott.nano.data.Configuration
import me.elliott.nano.services.InterviewService
import me.elliott.nano.services.Question
import me.elliott.nano.util.EmbedUtils

@CommandSet("Interviewee")
fun questionManagementCommands(interviewService: InterviewService, configuration: Configuration,
                               persistenceService: PersistenceService) = commands {

    command("next") {
        requiresGuild = false
        description = "Pulls the next question off the top of the queue."
        execute {
            val question = interviewService.questionQueue.dequeue()
                    ?: it.respond("There are no questions currently in the queue.").also { return@execute }

            return@execute it.respond(EmbedUtils.buildQuestionEmbed(question as Question))
        }
    }
}
