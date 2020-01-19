package me.elliott.nano.commands

import me.aberrantfox.kjdautils.api.dsl.command.*
import me.aberrantfox.kjdautils.internal.arguments.*
import me.elliott.nano.services.InterviewService

@CommandSet("Interview")
fun interviewSetupCommands(interviewService: InterviewService) = commands {
    command("StartInterview") {
        requiresGuild = true
        description = "Set the user to be interviewed."
        execute(UserArg("Interviewee"), SentenceArg("Bio")) {
            val (user, bio) = it.args

            if (interviewService.interviewInProgress())
                return@execute it.respond("There is already an interview in progress.")

            val response = interviewService.startInterview(it.guild!!, user, bio)

            it.respond(response)
        }
    }

    command("StopInterview") {
        description = "Stop a currently running interview."
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
