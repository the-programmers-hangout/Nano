package me.elliott.nano.commands

import com.gitlab.kordlib.common.entity.Snowflake
import me.elliott.nano.listeners.embedSent
import me.elliott.nano.services.*
import me.elliott.nano.util.Constants.Companion.INTERVIEWEE_CATEGORY
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.arguments.BooleanArg
import me.jakejmattson.discordkt.api.arguments.IntegerArg
import me.jakejmattson.discordkt.api.dsl.commands
import me.jakejmattson.discordkt.api.extensions.sendPrivateMessage
import java.awt.Color

fun interviewCommands(interviewService: InterviewService, discord: Discord) = commands(INTERVIEWEE_CATEGORY) {
    command("Next") {
        requiresGuild = false
        description = "Pulls the next question off the top of the queue."
        execute {
            val question = interviewService.getNextQuestion()
                    ?: return@execute respond("There are no questions currently in the queue.")

            embedSent = false

            author.sendPrivateMessage {
                val author = discord.api.getUser(Snowflake(question.authorId))
                val authorName = author?.username ?: "Unknown user"

                color = Color.MAGENTA
                description = question.questionText
                footer {
                    text = "Asked by $authorName"
                    icon = author?.avatar?.url
                }
            }
        }
    }

    command("Peek") {
        requiresGuild = false
        description = "Looks at the next five questions in the queue."
        execute {
            if (interviewService.getQuestionCount() > 0) {
                val questions = interviewService.peekTopFive()

                author.sendPrivateMessage {
                    title = "Up Next"
                    color = Color.PINK

                    questions.forEachIndexed { index, question ->
                        val author = discord.api.getUser(Snowflake(question.authorId))
                        val authorName = author?.discriminator ?: "Unknown user"

                        field {
                            name = "(ID: **$index**) - ${authorName}'s Question:"
                            value = question.questionText
                        }
                    }
                }
            } else {
                author.sendPrivateMessage("There are no questions in the queue.")
            }

        }
    }

    command("makeNext") {
        requiresGuild = false
        description = "Takes the provided question ID and makes that the next question."
        execute(IntegerArg) {
            author.sendPrivateMessage(interviewService.swapQuestion(args.first))
        }
    }

    command("SendTyping") {
        requiresGuild = false
        description = "Enables or disables sending typing events to the answer channel."
        execute(BooleanArg("On or Off", "On", "Off")) {
            val isOn = args.first
            val response = if (isOn) "enabled" else "disabled"
            val interview = interviewService.retrieveInterview() ?: return@execute

            interview.sendTyping = isOn
            respond("Sending of typing events is now **$response**")
        }
    }

    command("Count") {
        requiresGuild = false
        description = "Reports how many questions are pending reply."
        execute {
            if (author.isBot!!) return@execute
            val count = interviewService.getQuestionCount()
            return@execute respond("There are ${if (count == 0) "no" else count} questions in the queue")
        }
    }
}
