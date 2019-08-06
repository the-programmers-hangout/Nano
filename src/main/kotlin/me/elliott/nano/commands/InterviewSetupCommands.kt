package me.elliott.nano.commands

import me.aberrantfox.kjdautils.api.dsl.CommandSet
import me.aberrantfox.kjdautils.api.dsl.commands
import me.aberrantfox.kjdautils.internal.command.arguments.SentenceArg
import me.aberrantfox.kjdautils.internal.command.arguments.UserArg
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

            interviewService.interview = interviewService.createInterview(it.guild!!, user, bio)
            return@execute it.unsafeRespond("**Success:** ${user.name}'s interview has started!")
        }
    }
}
