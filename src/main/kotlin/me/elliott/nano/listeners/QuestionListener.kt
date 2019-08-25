package me.elliott.nano.listeners

import com.google.common.eventbus.Subscribe
import me.elliott.nano.data.Configuration
import me.elliott.nano.services.*
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

class QuestionListener(private val configuration: Configuration, private val interviewService: InterviewService) {

    @Subscribe
    fun onGuildMessageReceivedEvent(event: GuildMessageReceivedEvent) {
        val author = event.author
        val guild = event.guild
        val channel = event.channel
        val messageText = event.message.contentRaw

        if (author.isBot) return

        val guildConfiguration = configuration.getGuildConfig(guild.id)!!

        if (!interviewService.interviewInProgress() || channel.id != guildConfiguration.participantChannelId) return

        if (messageText.startsWith(guildConfiguration.questionPrefix)) {
            val prefix = guildConfiguration.questionPrefix
            interviewService.queueQuestionForReview(Question(author.id, messageText.removePrefix(prefix), reviewed = false), guild)

            channel.sendMessage(EmbedService.buildQuestionSubmittedEmbed(author)).queue()
        }
    }
}