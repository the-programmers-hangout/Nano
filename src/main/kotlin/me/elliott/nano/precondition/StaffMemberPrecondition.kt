package me.elliott.nano.precondition

import com.gitlab.kordlib.core.entity.channel.TextChannel
import kotlinx.coroutines.flow.*
import me.elliott.nano.data.Configuration
import me.elliott.nano.util.Constants
import me.jakejmattson.discordkt.api.dsl.*

class produceIsStaffMemberPrecondition(private val configuration: Configuration) : Precondition() {
    override suspend fun evaluate(event: CommandEvent<*>): PreconditionResult {
        val command = event.command ?: return Fail()
        if (command.category != Constants.INTERVIEWEE_CATEGORY) return Pass
        if (event.channel !is TextChannel) return Fail("**Failure:** This command must be executed in a text channel.")

        val guild = event.guild!!

        val staffRole = guild.roles.filter { it.name == configuration.staffRoleName }.firstOrNull() ?: return Fail()

        val member = event.message.getAuthorAsMember() ?: return Fail()
        if (member.roles.toList().contains(staffRole))
            return Fail("Missing clearance to use this command.")

        return Pass
    }
}