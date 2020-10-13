package me.elliott.nano

import com.gitlab.kordlib.common.entity.Snowflake
import me.elliott.nano.data.Configuration
import me.elliott.nano.extensions.requiredPermissionLevel
import me.elliott.nano.services.PermissionsService
import me.elliott.nano.services.StatisticsService
import me.elliott.nano.utilities.Constants
import me.jakejmattson.discordkt.api.dsl.bot
import java.awt.Color
import kotlin.time.ExperimentalTime

@ExperimentalTime
suspend fun main() {
    val token = System.getenv("BOT_TOKEN") ?: null
    val prefix = System.getenv("DEFAULT_PREFIX") ?: "<none>"
    require(token != null) { "Expected to find the bot token in the BOT_TOKEN environment variable" }

    bot(token) {

        prefix {
            val configuration = discord.getInjectionObjects(Configuration::class)
            configuration.prefix ?: prefix
        }

        configure {
            allowMentionPrefix = true
            generateCommandDocs = true
            showStartupLog = true
            theme = Color(0x00BFFF)
        }

        permissions {
            if (guild == null) {

                if (command.names.any { it.toLowerCase() == "help" }) return@permissions true
                if (command.category != Constants.INTERVIEWEE_CATEGORY) return@permissions false

                val config = discord.getInjectionObjects(Configuration::class)
                val guildConfig = config.guild ?: return@permissions false
                val interview = guildConfig.interview ?: return@permissions false

                if (user.id.longValue != interview.interviewee) return@permissions false

                return@permissions true

            } else {
                val requiredPermissionLevel = command.requiredPermissionLevel
                val member = user.asMember(guild!!.id)

                val permissionsService = discord.getInjectionObjects(PermissionsService::class)
                return@permissions permissionsService.hasClearance(member, requiredPermissionLevel)
            }


        }

        mentionEmbed {
            val configuration = it.discord.getInjectionObjects(Configuration::class)
            val statsService = it.discord.getInjectionObjects(StatisticsService::class)
            val guildConfiguration = configuration.guild ?: return@mentionEmbed

            val staffRole = it.guild!!.getRole(Snowflake(guildConfiguration.staffRole))
            val loggingChannel = it.guild!!.getChannel(Snowflake(guildConfiguration.loggingChannel))
            val reviewChannel = it.guild!!.getChannel(Snowflake(guildConfiguration.reviewChannel))
            val participantChannel = it.guild!!.getChannel(Snowflake(guildConfiguration.participantChannel))
            val amaCategory = it.guild!!.getChannel(Snowflake(guildConfiguration.amaCategory))


            title = "Nano"
            description = "A Minimalistic Q&A Bot"

            color = it.discord.configuration.theme

            thumbnail {
                url = api.getSelf().avatar.url
            }

            field {
                name = "Prefix"
                value = it.prefix()
                inline = true
            }

            field {
                name = "Question Prefix"
                value = configuration.questionPrefix
                inline = true
            }

            field {
                name = "Ping"
                value = statsService.ping
                inline = true
            }

            field {

                name = "Configuration"
                value = "```" +
                        "Staff Role: ${staffRole.name}\n" +
                        "Logging Channel: ${loggingChannel.name}\n" +
                        "Review Channel: ${reviewChannel.name}\n" +
                        "Participant Channel: ${participantChannel.name}\n" +
                        "Q&A Category: ${amaCategory.name}\n" +
                        "```"
            }

            field {
                val versions = it.discord.versions

                name = "Bot Info"
                value = "```" +
                        "Version: 1.0.0\n" +
                        "DiscordKt: ${versions.library}\n" +
                        "Kord: ${versions.kord}\n" +
                        "Kotlin: ${versions.kotlin}" +
                        "```"
            }

            field {
                name = "Uptime"
                value = statsService.uptime
                inline = true
            }

            field {
                name = "Source"
                value = "[GitHub](https://github.com/the-programmers-hangout/Nano)"
                inline = true
            }
        }
    }
}
