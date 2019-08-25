package me.elliott.nano.commands

import me.aberrantfox.kjdautils.api.dsl.CommandSet
import me.aberrantfox.kjdautils.api.dsl.commands
import me.aberrantfox.kjdautils.internal.arguments.*
import me.elliott.nano.services.InterviewCreationResult
import me.elliott.nano.services.InterviewService
import net.dv8tion.jda.api.entities.User

@CommandSet("Interview")
fun interviewSetupCommands(interviewService: InterviewService) = commands {

    command("StartInterview") {
        requiresGuild = true
        description = "Set the user to be interviewed."
        expect(UserArg("Interviewee"), SentenceArg("Bio"))
        execute {
            val user = it.args.component1() as User
            val bio = it.args.component2() as String

            if (interviewService.interviewInProgress())
                return@execute it.respond("There is already an interview in progress.")

            val creationResult = interviewService.startInterview(it.guild!!, user, bio)

            val result = when (creationResult) {
                is InterviewCreationResult.Error -> creationResult.message
                is InterviewCreationResult.Success -> "**Success:** ${user.name}'s interview has started!"
            }

            it.respond(result)
        }
    }

    command("StopInterview") {
        description = "Stop a currently running interview ."
        execute {
            val wasStopped = interviewService.stopInterview()

            val response =
                if (wasStopped)
                    "**Success:** Interview has been stopped!"
                else
                    "**Failure:** Interview is not running!"

            it.respond(response)
        }
    }
}
