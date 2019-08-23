package me.elliott.nano.data

import me.aberrantfox.kjdautils.api.annotation.Data

data class GuildConfiguration(
    var guildId: String = "insert-id",
    var staffRoleName: String = "Staff",
    var reviewChannelId: String = "insert-id",
    var participantChannelId: String = "insert-id",
    var loggingChannel: String = "insert-id",
    var categoryId: String = "insert-id",
    var questionPrefix: String = "insert-prefix"
)

@Data("config/config.json")
data class Configuration(
    val prefix: String = "-",
    val generateDocsAtRuntime: Boolean = false,
    var guildConfigurations: MutableList<GuildConfiguration> = mutableListOf(GuildConfiguration())
) {
    fun hasGuildConfig(guildId: String) = getGuildConfig(guildId) != null
    fun getGuildConfig(guildId: String) = guildConfigurations.firstOrNull { it.guildId == guildId }
}
