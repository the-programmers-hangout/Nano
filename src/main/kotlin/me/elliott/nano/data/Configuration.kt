package me.elliott.nano.data

import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Role
import me.jakejmattson.discordkt.api.dsl.Data


data class Configuration(
        val botOwner: Long = 345541952500006912,
        var guild: GuildConfiguration? = null,
        var prefix: String? = null,
        var questionPrefix: String = "[Q&A]"
        ) : Data("config/config.json") {

    fun isSetup(): Boolean { return guild != null }

    fun setup(guild: Guild, prefix: String, questionPrefix: String, staffRole: Role,
              loggingChannel: Long, reviewChannel: Long, participantChannel: Long, amaCategory: Long) {

        val newConfiguration = GuildConfiguration (
                guild.id.longValue,
                staffRole.id.longValue,
                loggingChannel,
                reviewChannel,
                participantChannel,
                amaCategory,
                null
        )

        this.prefix = prefix
        this.questionPrefix = questionPrefix
        this.guild = newConfiguration

        save()
    }
}

data class GuildConfiguration (
        var guild: Long,
        var staffRole: Long,
        var loggingChannel: Long,
        var reviewChannel: Long,
        var participantChannel: Long,
        var amaCategory: Long,
        var interview: Interview?
)

data class Interview (
        var interviewee: Long,
        var answerChannel: Long,
        var sendTyping: Boolean = true,
        var questionReview: MutableMap<Long, Question> = mutableMapOf(),
        var questions: MutableList<Question> = mutableListOf(),
        var answeredQuestions: MutableMap<Long, Long> = mutableMapOf()
)

data class Question (
        var questionText: String,
        var author: Long
)