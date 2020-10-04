package me.elliott.nano

import me.elliott.nano.data.Configuration
import me.jakejmattson.discordkt.api.dsl.bot
import java.awt.Color

suspend fun main() {
    val token = System.getenv("BOT_TOKEN") ?: null
    require(token != null) { "Expected the bot token as an environment variable" }

    bot(token) {

        prefix {
            val configuration = discord.getInjectionObjects(Configuration::class)
            configuration.prefix
        }

        configure {
            allowMentionPrefix = true
            generateCommandDocs = true
            showStartupLog = true
            theme = Color(0x00BFFF)
        }
    }
}