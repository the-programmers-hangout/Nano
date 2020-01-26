package me.elliott.nano.commands

import me.aberrantfox.kjdautils.api.dsl.command.*
import me.aberrantfox.kjdautils.extensions.jda.sendPrivateMessage
import me.aberrantfox.kjdautils.internal.arguments.IntegerArg
import me.aberrantfox.kjdautils.internal.arguments.*
import me.elliott.nano.listeners.embedSent
import me.elliott.nano.services.*
import me.elliott.nano.util.Constants.Companion.INTERVIEWEE_CATEGORY

@CommandSet(INTERVIEWEE_CATEGORY)
fun interviewCommands(interviewService: InterviewService, embedService: EmbedService) = commands {
    command("Next") {
        requiresGuild = false
        description = "Pulls the next question off the top of the queue."
        execute {
            val question = interviewService.getNextQuestion()
                    ?: return@execute it.respond("There are no questions currently in the queue.")

            embedSent = false
            it.author.sendPrivateMessage(embedService.buildQuestionEmbed(question))
        }
    }

    command("Peek") {
        requiresGuild = false
        description = "Looks at the next five questions in the queue."
        execute {
            if (interviewService.getQuestionCount() > 0)
                it.author.sendPrivateMessage(embedService.buildPeekAheadEmbed(interviewService.peekTopFive()))
            else
                it.author.sendPrivateMessage("There are no questions in the queue.")
        }
    }

    command("makeNext") {
        requiresGuild = false
        description = "Takes the provided question ID and makes that the next question."
        execute(IntegerArg) {
            it.author.sendPrivateMessage(interviewService.swapQuestion(it.args.component1() as Int))
        }
    }

    command("SendTyping") {
        requiresGuild = false
        description = "Enables or disables sending typing events to the answer channel."
        execute(BooleanArg("On or Off", "On", "Off")) {
            val isOn = it.args.first
            val response = if (isOn) "enabled" else "disabled"
            val interview = interviewService.retrieveInterview() ?: return@execute

            interview.sendTyping = isOn
            it.respond("Sending of typing events is now **$response**")
        }
    }

    command("Count") {
        requiresGuild = false
        description = "Reports how many questions are pending reply."
        execute {
            if (it.author.isBot) return@execute
            val count = interviewService.getQuestionCount()
            return@execute it.respond("There are ${if (count == 0) "no" else count} questions in the queue")
        }
    }

    command("Later") {
        requiresGuild = false
        description = "Returns current question to the back of the queue."
        execute {
            if (it.author.isBot) return@execute
            val currentQuestion = interviewService.getCurrentQuestion()
                    ?: return@execute it.respond("There is no active question")
            if (interviewService.isQueueEmpty())
                return@execute it.respond("There are no more questions in the queue")

            interviewService.pushQuestionBack(currentQuestion)
            val nextQuestion = interviewService.getNextQuestion()
            it.author.sendPrivateMessage(embedService.buildQuestionEmbed(nextQuestion!!))
        }
    }
}
