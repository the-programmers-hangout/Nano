package me.elliott.nano.services

import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.extensions.jda.fullName
import me.elliott.nano.data.Configuration
import net.dv8tion.jda.api.entities.*

@Service
class LoggingService(private val configuration: Configuration) {
    fun interviewStarted(guild: Guild, user: User) =
        log(guild, "**Info ::** Interview with ${user.name} has started.")

    fun directMessagesClosedError(guild: Guild, user: User) =
        log(guild, "**Error ::** ${user.asMention}'s DMs are not open. Please instruct them to allow DMs and try again.")

    fun submittedQuestion(guild: Guild, user: User) =
        log(guild, "**Info ::** ${user.asMention} has submitted a question for review.")

    fun questionApproved(guild: Guild, user: User, submitter: User) =
        log(guild, "**Info ::** ${user.fullName()} approved ${submitter.asMention}'s question.")

    fun questionDenied(guild: Guild, user: User, submitter: User) =
        log(guild, "**Info ::** ${user.fullName()} denied ${submitter.asMention}'s question.")

    private fun log(guild: Guild, message: String) = retrieveLoggingChannel(guild)?.sendMessage(message)?.queue()

    private fun retrieveLoggingChannel(guild: Guild): TextChannel? {
        val channelId = configuration.loggingChannel.takeIf { it.isNotEmpty() } ?: return null
        return guild.jda.getTextChannelById(channelId)
    }
}
