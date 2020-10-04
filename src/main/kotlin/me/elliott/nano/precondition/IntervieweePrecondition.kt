package me.elliott.nano.precondition

import me.elliott.nano.services.InterviewService
import me.elliott.nano.util.Constants
import me.jakejmattson.discordkt.api.dsl.*

class isIntervieweePrecondition(private val interviewService: InterviewService) : Precondition() {
    override suspend fun evaluate(event: CommandEvent<*>): PreconditionResult {
        val command = event.command ?: return Fail()
        if (command.category != Constants.INTERVIEWEE_CATEGORY) return Pass


        val interview = interviewService.retrieveInterview()
                ?: return Fail("Interview is not running.")

        if (!interview.isBeingInterviewed(event.author))
            return Fail("You are not being interviewed.")

        return Pass
    }
}