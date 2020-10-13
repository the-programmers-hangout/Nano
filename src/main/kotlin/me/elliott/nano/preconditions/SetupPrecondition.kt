package me.elliott.nano.preconditions

import me.elliott.nano.data.Configuration
import me.jakejmattson.discordkt.api.dsl.*

class SetupPrecondition(private val configuration: Configuration) : Precondition() {
    override suspend fun evaluate(event: CommandEvent<*>): PreconditionResult {
        val command = event.command ?: return Fail()

        if (command.names.contains("Setup")) return Pass
        if (!configuration.isSetup()) return Fail("You must first use the `Setup` command.")

        return Pass
    }
}