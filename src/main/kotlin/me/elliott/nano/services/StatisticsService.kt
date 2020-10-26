package me.elliott.nano.services

import me.elliott.nano.data.Configuration
import me.elliott.nano.utilities.timeToString
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.annotations.Service
import java.util.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
@Service
class StatisticsService(private val configuration: Configuration, private val discord: Discord) {
    private var startTime: Date = Date()

    val uptime: String
        get() = timeToString(Date().time - startTime.time)


    val ping: String
        get() = "${discord.api.gateway.averagePing.inMilliseconds.toInt()} ms"

}