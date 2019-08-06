package me.elliott.nano.precondition


import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.*
import me.elliott.nano.services.InterviewService

private const val Category = "Interviewee"

@Precondition
fun isIntervieweePrecondition(interviewService: InterviewService) = exit@{ event: CommandEvent ->
    val command = event.container.commands[event.commandStruct.commandName] ?: return@exit Pass
    if (command.category != Category) return@exit Pass

    if (!interviewService.interviewRunning()) return@exit Fail("Interview is not running.")
    if (event.author.id == interviewService.interview!!.intervieweeId) return@exit Pass

    return@exit Fail("You are not being interviewed.")
}