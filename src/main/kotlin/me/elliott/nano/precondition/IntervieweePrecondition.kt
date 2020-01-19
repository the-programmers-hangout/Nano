package me.elliott.nano.precondition

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.*
import me.elliott.nano.services.InterviewService
import me.elliott.nano.util.Constants

@Precondition
fun isIntervieweePrecondition(interviewService: InterviewService) = precondition {
    val command = it.container[it.commandStruct.commandName] ?: return@precondition Pass
    if (command.category != Constants.INTERVIEWEE_CATEGORY) return@precondition Pass

    val interview = interviewService.retrieveInterview()
        ?: return@precondition Fail("Interview is not running.")

    if (!interview.isBeingInterviewed(it.author))
        return@precondition Fail("You are not being interviewed.")

    return@precondition Pass
}