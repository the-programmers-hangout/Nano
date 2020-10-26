package me.elliott.nano.services

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.channel.TextChannel
import me.elliott.nano.data.Configuration
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.annotations.Service

@Service
class LoggerService(private val configuration: Configuration, private val discord: Discord) {
    suspend fun log(message: String) {

        val guildConfig = configuration.guild ?: return
        val loggingChannel = discord.api.getChannelOf<TextChannel>(Snowflake(guildConfig.loggingChannel)) ?: return

        loggingChannel.createMessage(message)
    }
}