package me.elliott.nano.precondition

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.*
import me.elliott.nano.services.InterviewService
import me.elliott.nano.util.Constants

@Precondition
fun isIntervieweePrecondition(interviewService: InterviewService) = exit@{ event: CommandEvent ->
    val command = event.container.commands[event.commandStruct.commandName] ?: return@exit Pass
    if (command.category != Constants.INTERVIEWEE) return@exit Pass

    val interview = interviewService.retrieveInterview()
        ?: return@exit Fail("Interview is not running.")

    if (event.author.id != interview.intervieweeId)
        return@exit Fail("You are not being interviewed.")

    return@exit Pass
}