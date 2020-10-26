package me.elliott.nano.conversations

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.behavior.channel.createEmbed
import com.gitlab.kordlib.core.entity.channel.TextChannel
import com.gitlab.kordlib.kordx.emoji.Emojis
import com.gitlab.kordlib.kordx.emoji.toReaction
import me.elliott.nano.data.Configuration
import me.elliott.nano.data.Interview
import me.elliott.nano.data.Question
import me.elliott.nano.extensions.workingWidth
import me.jakejmattson.discordkt.api.arguments.EveryArg
import me.jakejmattson.discordkt.api.dsl.conversation
import java.awt.Color

class AnswerConversation(private val configuration: Configuration) {
    fun createAnswerConversation(interview: Interview, question: Question) = conversation {
        val questionAuthor = discord.api.getUser(Snowflake(question.author)) ?: return@conversation
        val questionAuthorName = questionAuthor.username

        respond("**Question from $questionAuthorName:** ${question.questionText}")

        val firstMessage = promptUntil(
                argumentType = EveryArg,
                prompt = "**Response (type `cancel` to return): **",
                error = "The first response can only be ${user.workingWidth()} in length.",
                isValid = { message ->
                    message.length <= user.workingWidth()
                }

        )

        if (firstMessage.toLowerCase().startsWith("cancel"))
            return@conversation


        val answerChannel = discord.api.getChannelOf<TextChannel>(Snowflake(interview.answerChannel)) ?: return@conversation
        val answerMessage = answerChannel.createEmbed {
            title = "${user.username} is answering $questionAuthorName's Question:"
            color = Color.MAGENTA
            description = "**Question:** ${question.questionText}"

            footer {
                text = "Asked by $questionAuthorName"
                icon = questionAuthor.avatar.url
            }
        }

        answerMessage.addReaction(Emojis.star.toReaction())

        val firstResponseMessage = answerChannel.createMessage("**${user.username}:** $firstMessage")
        val firstMessageResponse = channel.getMessage(previousUserMessageId)
        interview.answeredQuestions[firstMessageResponse.id.longValue] = firstResponseMessage.id.longValue

        var questionsEnded = false
        while (!questionsEnded) {
            val message = promptMessage(EveryArg, "**Continued response (type `end` to finish question):**")

            if (message.toLowerCase().startsWith("end")) {
                questionsEnded = true
            } else {
                val responseMessage = answerChannel.createMessage(message)
                val messageResponse = channel.getMessage(previousUserMessageId)

                interview.answeredQuestions[messageResponse.id.longValue] = responseMessage.id.longValue
            }
        }

        interview.questions.removeFirst()

        configuration.save()

        respond("Responded! You can edit the above message(s) if you wish to make changes")
    }
}