package me.elliott.nano.services

import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.discord.Discord
import me.elliott.nano.extensions.toEmbedBuilder
import net.dv8tion.jda.api.entities.*
import java.awt.Color

@Service
class EmbedService(private val discord: Discord) {
    private fun retrieveUserById(id: String) = discord.jda.getUserById(id)

    fun buildQuestionEmbed(question: Question): MessageEmbed {
        val author = retrieveUserById(question.authorId)
        val authorName = author?.name ?: "Unknown user"

        return embed {
            title = "$authorName's Question:"
            color = Color.MAGENTA
            description = question.questionText
        }
    }

    fun buildQuestionReviewEmbed(question: Question): MessageEmbed {
        val author = retrieveUserById(question.authorId)
        val authorName = author?.name ?: "Unknown user"

        return embed {
            title = "$authorName's Question:"
            color = Color.LIGHT_GRAY
            description = question.questionText
        }
    }

    fun buildResponseEmbed(interviewee: User, question: Question): MessageEmbed {
        val author = retrieveUserById(question.authorId)
        val authorName = author?.name ?: "Unknown user"

        return embed {
            title = "${interviewee.name} is answering $authorName's Question:"
            color = Color.MAGENTA
            description = "**Question:** ${question.questionText}"
        }.toEmbedBuilder().setFooter("Asked by $authorName", author?.effectiveAvatarUrl).build()
    }

    companion object {
        fun buildInterviewStartEmbed(interviewee: User, bio: String, questionPrefix: String) =
            embed {
                title = "AMA Started - Please Submit Your Questions Below."
                color = Color.CYAN
                description = bio
                thumbnail = interviewee.avatarUrl
                addBlankField(false)
                field {
                    name = "Please begin your question with the following prefix: $questionPrefix"
                    value = "**Example:** $questionPrefix What's one of your favorite technologies?"
                }
            }.toEmbedBuilder().setAuthor(interviewee.name).build()

        fun buildInterviewInstructionEmbed(prefix: String, botAvatarUrl: String) =
            embed {
                thumbnail = botAvatarUrl
                title = "How Do I Answer Questions?"
                description = "When you're ready for the next question, simply " +
                    "type: `${prefix.plus("next")}`. Whenever a question is displayed, " +
                    "any replies you choose to provide will be sent to the interview answer channel until " +
                    "you request the next question or end the interview."

                field {
                    name = "Ending The Interview"
                    value = "When you're ready to end the interview, message a moderator and they will end it for you."
                    inline = true
                }

                field {
                    name = "Turn Typing Events On or Off"
                    value = "If you want the bot to send your typing events to the answer channel, type " +
                        "`${prefix.plus("SendTyping")} on/off` *(**On** by default)*"
                    inline = true
                }
                color = Color.MAGENTA
            }

        fun buildQuestionSubmittedEmbed(user: User) =
            embed {
                title = "Question Submitted"
                color = Color.PINK
                description = "**${user.name}**'s question was successfully submitted for review."
            }
    }
}