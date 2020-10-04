package me.elliott.nano.data

import me.jakejmattson.discordkt.api.dsl.Data

data class Configuration(
        val prefix: String = "-",
        var guildId: String = "insert-id",
        var staffRoleName: String = "Staff",
        var reviewChannelId: String = "insert-id",
        var participantChannelId: String = "insert-id",
        var loggingChannel: String = "insert-id",
        var categoryId: String = "insert-id",
        var questionPrefix: String = "insert-prefix"
) : Data("config/config.json")