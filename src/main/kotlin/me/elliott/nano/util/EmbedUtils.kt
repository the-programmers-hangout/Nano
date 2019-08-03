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
                    title("AMA Started - Please Submit Your Questions In ${participantChannel.asMention}")
                    setColor(Color.CYAN)
                    description("**Bio:** $bio")
                    setThumbnail(interviewee.avatarUrl)
                    setAuthor(interviewee.name)
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
                    description("**${user.asMention}**'s question was successfully submitted for review.")
                }

        fun buildQuestionReviewEmbed(question: Question) =
                embed {
                    title("${question.event.author.asMention}'s Question:")
                    setColor(Color.LIGHT_GRAY)
                    description(question.questionText)
                }
    }
}