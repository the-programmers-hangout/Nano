package me.elliott.nano.services

import me.aberrantfox.kjdautils.api.annotation.Service
import me.elliott.nano.data.Configuration
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User

@Service
class LoggingService(private val config: Configuration) {

    private fun withLog(guild: Guild, f: () -> String) =
        getLogConfig(guild.id).apply {
            log(guild, getLogConfig(guild.id), f())
        }

    fun interviewStarted(guild: Guild, user: User) = withLog(guild) {
        "**Info ::** Interview with ${user.name} has started."
    }

    fun submittedQuestion(guild: Guild, user: User) = withLog(guild) {
        "**Info ::** ${user.asMention} has submitted a question for review."
    }

    fun questionApproved(guild: Guild, user: User, submitter: User) = withLog(guild) {
        "**Info ::** ${user.asMention} approved ${submitter.asMention}'s question."
    }

    fun questionDenied(guild: Guild, user: User, submitter: User) = withLog(guild) {
        "**Info ::** ${user.asMention} denied ${submitter.asMention}'s question."
    }

    private fun getLogConfig(guildId: String) = config.getGuildConfig(guildId)!!.loggingChannel
    private fun log(guild: Guild, logChannelId: String, message: String) =
        logChannelId.takeIf { it.isNotEmpty() }?.idToTextChannel(guild)
            ?.sendMessage(message)?.queue()

    private fun String.idToTextChannel(guild: Guild) = guild.jda.getTextChannelById(this)
}
