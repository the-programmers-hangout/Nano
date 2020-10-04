package me.elliott.nano.services

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.behavior.getChannelOf
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.User
import com.gitlab.kordlib.core.entity.channel.TextChannel
import me.elliott.nano.data.Configuration
import me.jakejmattson.discordkt.api.annotations.Service

@Service
class LoggingService(private val configuration: Configuration) {
    suspend fun interviewStarted(guild: Guild, user: User) =
        log(guild, "**Info ::** Interview with ${user.username} has started.")

    suspend fun directMessagesClosedError(guild: Guild, user: User) =
        log(guild, "**Error ::** ${user.mention}'s DMs are not open. Please instruct them to allow DMs and try again.")

    suspend fun submittedQuestion(guild: Guild, user: User) =
        log(guild, "**Info ::** ${user.mention} has submitted a question for review.")

    suspend fun questionApproved(guild: Guild, user: User, submitter: User) =
        log(guild, "**Info ::** ${user.mention} approved ${submitter.mention}'s question.")

    suspend fun questionDenied(guild: Guild, user: User, submitter: User) =
        log(guild, "**Info ::** ${user.mention} denied ${submitter.mention}'s question.")

    private suspend fun log(guild: Guild, message: String) = retrieveLoggingChannel(guild)?.createMessage(message)

    private suspend fun retrieveLoggingChannel(guild: Guild): TextChannel? {
        val channelId = configuration.loggingChannel.takeIf { it.isNotEmpty() } ?: return null
        return guild.getChannelOf<TextChannel>(Snowflake(channelId))
    }
}
