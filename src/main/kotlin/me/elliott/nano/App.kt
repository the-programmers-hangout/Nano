package me.elliott.nano

import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.startBot
import me.aberrantfox.kjdautils.discord.Discord
import me.elliott.nano.data.Configuration
import me.elliott.nano.services.InterviewService

fun main(args: Array<String>) {
    val token = args.firstOrNull()
            ?: throw IllegalArgumentException("No program arguments provided. Expected bot token.")

    startBot(token) {
        configure {
            globalPath = "me.elliott.nano"
            allowPrivateMessages = true
        }
    }

    @Service
    class PrefixService(configuration: Configuration, discord: Discord) {
        init {
            discord.configuration.prefix = configuration.prefix


        }
    }
}