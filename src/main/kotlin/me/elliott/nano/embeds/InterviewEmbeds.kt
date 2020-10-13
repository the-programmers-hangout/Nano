package me.elliott.nano.embeds

import com.gitlab.kordlib.core.entity.User
import com.gitlab.kordlib.rest.builder.message.EmbedBuilder
import java.awt.Color

fun EmbedBuilder.interviewStarted(bio: String, interviewee: User, questionPrefix: String) {
    title = "AMA Started - Please Submit Your Questions Below."
    color = Color.CYAN
    description = bio
    thumbnail {
        url = interviewee.avatar.url
    }

    field {  }

    field {
        name = "Please begin your question with the following prefix: $questionPrefix"
        value = "**Example:** $questionPrefix What's one of your favorite technologies?"
    }
}

fun EmbedBuilder.interviewStartedDM(avatar: String, prefix: String) {
    thumbnail {
        url = avatar
    }

    title = "How Do I Answer Questions?"
    description = "When you're ready for the next question, simply " +
            "type: `${prefix.plus("answer")}`. Whenever a question is displayed, " +
            "any replies you choose to provide will be sent to the interview answer channel until " +
            "you end the question or end the interview."

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

fun EmbedBuilder.reviewEmbed(questionText: String, author: User, embedColor: Color) {
    color = embedColor
    description = questionText
    footer {
        text = "Asked by ${author.username}"
        icon = author.avatar.url
    }
}