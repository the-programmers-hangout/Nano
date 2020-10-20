package me.elliott.nano.preconditions

import me.elliott.nano.data.Configuration
import me.jakejmattson.discordkt.api.dsl.precondition


fun setupPrecondition(configuration: Configuration) = precondition {
    val command = command ?: return@precondition fail()
    if (configuration.isSetup()) return@precondition

    if (!command.names.any { it.toLowerCase() == "setup" }) fail("You must first use the `Setup` command.")
}