package me.elliott.nano.precondition

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.*
import me.elliott.nano.data.Configuration
import me.elliott.nano.util.Constants

import net.dv8tion.jda.api.entities.TextChannel

@Precondition
fun produceIsStaffMemberPrecondition(configuration: Configuration) = exit@{ event: CommandEvent ->
    val command = event.container.commands[event.commandStruct.commandName] ?: return@exit Pass

    if (command.category == Constants.INTERVIEWEE_CATEGORY) return@exit Pass
    if (event.channel !is TextChannel) return@exit Fail("**Failure:** This command must be executed in a text channel.")

    val guild = event.guild!!
    val guildConfig = configuration.getGuildConfig(guild.id) ?: return@exit Pass
    val staffRole = guild.getRolesByName(guildConfig.staffRoleName, true).firstOrNull() ?: return@exit Fail()

    if (staffRole !in event.message.member!!.roles)
        return@exit Fail("Missing clearance to use this command.")

    return@exit Pass
}
