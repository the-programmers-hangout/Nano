package me.elliott.nano.commands

import me.elliott.nano.services.InterviewService
import me.jakejmattson.discordkt.api.arguments.EveryArg
import me.jakejmattson.discordkt.api.arguments.UserArg
import me.jakejmattson.discordkt.api.dsl.commands

fun interviewSetupCommands(interviewService: InterviewService) = commands("Interview") {
    command("StartInterview") {
        requiresGuild = true
        description = "Set the user to be interviewed."
        execute(UserArg("Interviewee"), EveryArg("Bio")) {
            val (user, bio) = args

            if (interviewService.interviewInProgress())
                return@execute respond("There is already an interview in progress.")

            val response = interviewService.startInterview(guild!!, user, bio)

            respond(response)
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

            respond(response)
        }
    }
}
