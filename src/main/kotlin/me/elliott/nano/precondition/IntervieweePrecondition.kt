package me.elliott.nano.precondition


import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.*
import me.elliott.nano.data.Configuration
import me.elliott.nano.services.InterviewService

private const val Category = "Interviewee"

@Precondition
fun isIntervieweeAndCorrectChannelPrecondition(configuration: Configuration,
                                               interviewService: InterviewService) = exit@{ event: CommandEvent ->
    val command = event.container.commands[event.commandStruct.commandName] ?: return@exit Pass
    if (command.category != Category) return@exit Pass

    val interview = interviewService.getInterview()

    if (event.author.id == interview.intervieweeId
            && interview.answerChannel == event.channel.id) return@exit Pass

    return@exit Fail("You are not being interviewed, or you executed this command outside of the appropriate channel.")
}