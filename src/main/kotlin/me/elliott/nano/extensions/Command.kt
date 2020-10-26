package me.elliott.nano.extensions

import me.elliott.nano.services.DEFAULT_REQUIRED_PERMISSION
import me.elliott.nano.services.Permission
import me.jakejmattson.discordkt.api.dsl.Command
import java.util.*

private object CommandPropertyStore {
    val permissions = WeakHashMap<Command, Permission>()
}

var Command.requiredPermissionLevel: Permission
    get() = CommandPropertyStore.permissions[this] ?: DEFAULT_REQUIRED_PERMISSION
    set(value) {
        CommandPropertyStore.permissions[this] = value
    }