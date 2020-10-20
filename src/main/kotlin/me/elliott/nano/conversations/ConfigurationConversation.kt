package me.elliott.nano.conversations

import com.gitlab.kordlib.core.entity.Guild
import me.elliott.nano.data.Configuration
import me.jakejmattson.discordkt.api.arguments.CategoryArg
import me.jakejmattson.discordkt.api.arguments.ChannelArg
import me.jakejmattson.discordkt.api.arguments.EveryArg
import me.jakejmattson.discordkt.api.arguments.RoleArg
import me.jakejmattson.discordkt.api.dsl.Conversation
import me.jakejmattson.discordkt.api.dsl.conversation


class ConfigurationConversation(private val configuration: Configuration) {
    fun createConfigurationConversation(guild: Guild) = conversation {
        val prefix = promptMessage(EveryArg, "Bot prefix:")
        val questionPrefix = promptMessage(EveryArg, "Question prefix:")
        val staffRole = promptMessage(RoleArg("Role", guild.id), "Staff role:")
        val loggingChannel = promptMessage(ChannelArg, "Logging channel:").id.longValue
        val reviewChannel = promptMessage(ChannelArg, "Review channel:").id.longValue
        val participantChannel = promptMessage(ChannelArg, "Participant channel:").id.longValue
        val amaCategory = promptMessage(CategoryArg, "Q&A category:").id.longValue

        configuration.setup(guild, prefix, questionPrefix, staffRole, loggingChannel, reviewChannel,
                participantChannel, amaCategory)
    }
}
