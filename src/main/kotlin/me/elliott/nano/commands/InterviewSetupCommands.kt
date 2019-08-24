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
        expect(UserArg, SentenceArg)
        execute {
            val user = it.args.component1() as User
            val bio = it.args.component2() as String

            when (val result = interviewService.createInterview(it.guild!!, user, bio)) {
                is InterviewCreationResult.Error -> {
                    it.respond(result.message)
                }
                is InterviewCreationResult.Success -> {
                    it.unsafeRespond("**Success:** ${user.name}'s interview has started!")
                }
            }
        }
    }

    command("StopInterview") {
        description = "Stop a currently running interview ."
        execute {
            val wasStopped = interviewService.stopInterview()

            it.respond(if (wasStopped) "**Success:** Interview has been stopped!" else "**Failure :** Interview is not running!")
        }
    }
}
