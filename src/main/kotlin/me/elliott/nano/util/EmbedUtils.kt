package me.elliott.nano.util

import me.aberrantfox.kjdautils.api.dsl.embed
import me.elliott.nano.services.Question
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import java.awt.Color

class EmbedUtils {
    companion object {

        fun buildInterviewStartEmbed(interviewee: User, participantChannel: TextChannel, bio: String) =
                embed {
                    title("AMA Started - Please Submit Your Questions Below.")
                    setColor(Color.CYAN)
                    description(bio)
                    setThumbnail(interviewee.avatarUrl)
                    setAuthor(interviewee.name)
                    addBlankField(false)
                    field {
                        name = "Please begin your question with the following prefix: [Q&A]"
                        value =  "**Example:** [Q&A] What's one of your favorite technologies?"
                    }
                }

        fun buildNotCompleteEmbed() =
                embed {
                    title("Error")
                    setColor(Color.RED)
                    description("**Please add an interviewee and answer channel to start an AMA.")
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
                    setThumbnail(question.event.author.avatarUrl)
                    setColor(Color.LIGHT_GRAY)
                    description(question.questionText)
                }

        fun buildQuestionEmbed(question: Question) =
                embed {
                    title("${question.event.author.name}'s Question:")
                    setThumbnail(question.event.author.avatarUrl)
                    setColor(Color.MAGENTA)
                    description(question.questionText)
                }
    }
}