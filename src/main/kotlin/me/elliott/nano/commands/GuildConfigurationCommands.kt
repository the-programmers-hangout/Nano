package me.elliott.nano.commands

import me.elliott.nano.conversations.ConfigurationConversation
import me.elliott.nano.data.Configuration
import me.elliott.nano.extensions.requiredPermissionLevel
import me.elliott.nano.services.Permission
import me.jakejmattson.discordkt.api.arguments.AnyArg
import me.jakejmattson.discordkt.api.arguments.CategoryArg
import me.jakejmattson.discordkt.api.arguments.ChannelArg
import me.jakejmattson.discordkt.api.arguments.RoleArg
import me.jakejmattson.discordkt.api.dsl.commands
import me.jakejmattson.discordkt.api.services.ConversationService

fun guildConfigurationCommands(conversationService: ConversationService, configuration: Configuration) = commands("Guild Configuration") {
    guildCommand("Setup") {
        description = "Setup a guild to use Nano"
        requiredPermissionLevel = Permission.GUILD_OWNER
        execute {

            if (configuration.isSetup()) {
                respond("Configuration already exists. You can use commands to modify the config")
                return@execute
            }

            conversationService.startPublicConversation<ConfigurationConversation>(author, channel.asChannel(), guild)
            respond("${guild.name} has been setup")
        }
    }

    guildCommand("Prefix") {
        description = "Set the prefix required for the bot to register a command."
        requiredPermissionLevel = Permission.STAFF
        execute(AnyArg("Prefix")) {
            val prefix = args.first

            configuration.prefix = prefix
            configuration.save()

            respond("Prefix set to: $prefix")
        }
    }

    guildCommand("QuestionPrefix") {
        description = "Set the prefix required for the bot to register a question."
        requiredPermissionLevel = Permission.STAFF
        execute(AnyArg("QuestionPrefix")) {
            val questionPrefix = args.first

            configuration.questionPrefix = questionPrefix
            configuration.save()

            respond("Question prefix set to: $questionPrefix")
        }
    }

    guildCommand("StaffRole") {
        description = "Set the role required to use this bot."
        requiredPermissionLevel = Permission.STAFF
        execute(RoleArg) {
            val requiredRole = args.first

            configuration.guild!!.staffRole = requiredRole.id.longValue
            configuration.save()

            respond("Staff role set to ${requiredRole.name}")
        }
    }

    guildCommand("LoggingChannel") {
        description = "Set the channel where logs will be output."
        requiredPermissionLevel = Permission.STAFF
        execute(ChannelArg) {
            val logChannel = args.first

            configuration.guild!!.loggingChannel = logChannel.id.longValue
            configuration.save()

            respond("Required role set to ${logChannel.name}")
        }
    }

    guildCommand("ReviewChannel") {
        description = "Set the channel where question reviews will be output."
        requiredPermissionLevel = Permission.STAFF
        execute(ChannelArg) {
            val reviewChannel = args.first

            configuration.guild!!.reviewChannel = reviewChannel.id.longValue
            configuration.save()

            respond("Review channel set to ${reviewChannel.name}")
        }
    }

    guildCommand("ParticipantChannel") {
        description = "Set the channel where participants can take part."
        requiredPermissionLevel = Permission.STAFF
        execute(ChannelArg) {
            val participantChannel = args.first

            configuration.guild!!.participantChannel = participantChannel.id.longValue
            configuration.save()

            respond("Participant channel set to ${participantChannel.name}")
        }
    }

    guildCommand("AMACategory") {
        description = "Set the category where Q&A channels will be created."
        requiredPermissionLevel = Permission.STAFF
        execute(CategoryArg) {
            val amaCategory = args.first

            configuration.guild!!.amaCategory = amaCategory.id.longValue
            configuration.save()

            respond("Q&A Category set to ${amaCategory.name}")
        }
    }
}