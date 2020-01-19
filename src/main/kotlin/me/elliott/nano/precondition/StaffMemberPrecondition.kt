package me.elliott.nano.precondition

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.*
import me.elliott.nano.data.Configuration
import me.elliott.nano.util.Constants

import net.dv8tion.jda.api.entities.TextChannel

@Precondition
fun produceIsStaffMemberPrecondition(configuration: Configuration) = precondition {
    val command = it.container[it.commandStruct.commandName] ?: return@precondition Pass

    if (command.category == Constants.INTERVIEWEE_CATEGORY) return@precondition Pass
    if (it.channel !is TextChannel) return@precondition Fail("**Failure:** This command must be executed in a text channel.")

    val guild = it.guild!!
    val staffRole = guild.getRolesByName(configuration.staffRoleName, true).firstOrNull() ?: return@precondition Fail()

    if (staffRole !in it.message.member!!.roles)
        return@precondition Fail("Missing clearance to use this command.")

    return@precondition Pass
}
