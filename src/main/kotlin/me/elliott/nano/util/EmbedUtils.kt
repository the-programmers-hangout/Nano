package me.elliott.nano.util

import me.aberrantfox.kjdautils.api.dsl.embed
import me.elliott.nano.services.Question
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import java.awt.Color

class EmbedUtils {
    companion object {

        fun buildInterviewStartEmbed(interviewee: User, participantChannel: TextChannel, bio: String, questionPrefix: String) =
                embed {
                    title("AMA Started - Please Submit Your Questions Below.")
                    setColor(Color.CYAN)
                    description(bio)
                    setThumbnail(interviewee.avatarUrl)
                    setAuthor(interviewee.name)
                    addBlankField(false)
                    field {
                        name = "Please begin your question with the following prefix: $questionPrefix"
                        value = "**Example:** $questionPrefix What's one of your favorite technologies?"
                    }
                }

        fun buildNotCompleteEmbed() =
                embed {
                    title("Error")
                    setColor(Color.RED)
                    description("**Please add an interviewee and answer channel to start an AMA.")
                }

        fun buildInterviewInstructionEmbed(prefix: String, botAvatarUrl: String) =
                embed {
                    setThumbnail(botAvatarUrl)
                    title("How Do I Answer Questions?")
                    description("When you're ready for the next question, simply " +
                            "type: `${prefix.plus("next")}`. Whenever a question is displayed, " +
                            "any replies you choose to provide will be sent to the interview answer channel until " +
                            "you request the next question or end the interview.")

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
                    setColor(Color.MAGENTA)
                }

        fun buildQuestionSubmittedEmbed(user: User) =
                embed {
                    title("Question Submitted")
                    setColor(Color.PINK)
                    description("**${user.name}**'s question was successfully submitted for review.")
                }

        fun buildQuestionReviewEmbed(question: Question) =
                embed {
                    title("${question.event.author.name}'s Question:")
                    setColor(Color.LIGHT_GRAY)
                    description(question.questionText)
                }

        fun buildResponseEmbed(interviewee: User, question: Question) =
                embed {
                    title("${interviewee.name} is answering ${question.event.author.name}'s Question:")
                    setColor(Color.MAGENTA)
                    description("**Question:** ${question.questionText}")
                    setFooter("Asked by ${question.event.author.name}", question.event.author.avatarUrl)
                }

        fun buildQuestionEmbed(question: Question) =
                embed {
                    title("${question.event.author.name}'s Question:")
                    setColor(Color.MAGENTA)
                    description(question.questionText)
                }
    }
}