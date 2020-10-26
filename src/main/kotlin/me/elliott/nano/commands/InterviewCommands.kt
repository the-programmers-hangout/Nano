package me.elliott.nano.commands

import com.gitlab.kordlib.common.entity.Snowflake
import me.elliott.nano.conversations.AnswerConversation
import me.elliott.nano.data.Configuration
import me.elliott.nano.services.InterviewService
import me.elliott.nano.utilities.Constants.Companion.INTERVIEWEE_CATEGORY
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.arguments.BooleanArg
import me.jakejmattson.discordkt.api.arguments.IntegerArg
import me.jakejmattson.discordkt.api.dsl.commands
import me.jakejmattson.discordkt.api.extensions.sendPrivateMessage
import java.awt.Color

fun interviewCommands(interviewService: InterviewService, discord: Discord, configuration: Configuration) = commands(INTERVIEWEE_CATEGORY) {
    dmCommand("Answer") {
        description = "Answers the question on top of the queue."
        execute {
            val interview = configuration.guild!!.interview ?: return@execute

            val question = interviewService.getNextQuestion()
            if (question == null) {
                respond("There are no questions currently in the queue.")
                return@execute
            }

            AnswerConversation(configuration)
                    .createAnswerConversation(interview, question)
                    .startPrivately(discord, author)
        }
    }

    dmCommand("Next") {
        description = "Pulls the next question off the top of the queue."
        execute {
            val question = interviewService.getNextQuestion()

            if (question == null) {
                respond("There are no questions currently in the queue.")
                return@execute
            }

            author.sendPrivateMessage {
                val author = discord.api.getUser(Snowflake(question.author)) ?: return@sendPrivateMessage
                val authorName = author.username

                color = Color.MAGENTA
                description = question.questionText
                footer {
                    text = "Asked by $authorName"
                    icon = author.avatar.url
                }
            }
        }
    }

    dmCommand("Peek") {
        description = "Looks at the next five questions in the queue."
        execute {

            val user = author

            if (interviewService.getQuestionCount() > 0) {
                val questions = interviewService.peekTopFive()

                user.sendPrivateMessage {
                    title = "Up Next"
                    color = Color.PINK

                    questions.forEachIndexed { index, question ->
                        field {
                            name = "(ID: **$index**) - ${user.username}'s Question:"
                            value = question.questionText
                        }
                    }
                }
            } else {
                user.sendPrivateMessage("There are no questions in the queue.")
            }

        }
    }

    dmCommand("MakeNext") {
        description = "Takes the provided question ID and makes that the next question."
        execute(IntegerArg) {
            author.sendPrivateMessage(interviewService.swapQuestion(args.first))
        }
    }


    dmCommand("SendTyping") {
        description = "Enables or disables sending typing events to the answer channel."
        execute(BooleanArg("On or Off", "On", "Off")) {
            val isOn = args.first
            val response = if (isOn) "enabled" else "disabled"

            val interview = configuration.guild?.interview ?: return@execute

            interview.sendTyping = isOn
            configuration.save()
            respond("Sending of typing events is now **$response**")
        }
    }

    dmCommand("Count") {
        description = "Reports how many questions are pending reply."
        execute {
            val count = interviewService.getQuestionCount()

            respond("There are ${if (count == 0) "no" else count} questions in the queue")
        }
    }

    dmCommand("Later") {
        description = "Returns current question to the back of the queue."
        execute {
            respond(interviewService.pushQuestionBack())
        }
    }
}