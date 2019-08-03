package me.elliott.nano

import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.dsl.KConfiguration
import me.aberrantfox.kjdautils.api.startBot
import me.elliott.nano.data.Configuration

fun main(args: Array<String>) {
    val token = args.firstOrNull() ?: return println("No token provided!")

    startBot(token) {

        configure {
            globalPath = "me.elliott.nano"
            registerInjectionObject(this@startBot.container)
            registerInjectionObject(this)
        }
    }
}

@Service
class PrefixLoader(kjdaConfiguration: KConfiguration, configuration: Configuration) {
    init {
        kjdaConfiguration.prefix = configuration.prefix
    }
}