package me.elliott.nano.services

import me.aberrantfox.kjdautils.api.annotation.Service
import me.elliott.nano.data.Configuration
import me.elliott.nano.extensions.toEmbedBuilder
import me.elliott.nano.util.Constants
import net.dv8tion.jda.api.entities.*
import java.awt.Color
import java.util.ArrayDeque

data class Interview(
        private var intervieweeId: String,
        var answerChannel: String,
        var bio: String,
        var sendTyping: Boolean = true
) {
    fun isBeingInterviewed(user: User) = user.id == intervieweeId
}

data class Question(val authorId: String, val questionText: String)

@Service
class InterviewService(private val configuration: Configuration,
                       private val embedService: EmbedService,
                       private val loggingService: LoggingService) {

    private var questionReviewStore = mutableMapOf<String, Question>()
    private var questionQueue = ArrayDeque<Question>()
    private var interview: Interview? = null

    fun retrieveInterview() = interview
    fun interviewInProgress() = interview != null
    fun getCurrentQuestion(): Question? = questionQueue.peek()
    fun getNextQuestion(): Question? = if (questionQueue.isEmpty()) null else questionQueue.removeFirst()

    fun startInterview(guild: Guild, interviewee: User, bio: String): InterviewCreationResult {
        val guildConfiguration = configuration.getGuildConfig(guild.id)
            ?: return InterviewCreationResult.Error(Constants.MISSING_GUILD_CONFIG)

        val botCategory = guild.getCategoryById(guildConfiguration.categoryId)
            ?: return InterviewCreationResult.Error(Constants.MISSING_CATEGORY_CONFIG)

        val answerChannel = botCategory.createTextChannel(interviewee.name).complete().id

        val participantChannel = guild.jda.getTextChannelById(guildConfiguration.participantChannelId)
            ?: return InterviewCreationResult.Error(Constants.MISSING_PARTICIPANT_CONFIG)

        participantChannel.sendMessage(EmbedService.buildInterviewStartEmbed(interviewee, bio,
            guildConfiguration.questionPrefix)).queue {
            it.channel.pinMessageById(it.id).queue()
        }

        val privateChannel = interviewee.openPrivateChannel().complete()
            ?: return InterviewCreationResult.Error(Constants.DM_CLOSED_ERROR).also {
                loggingService.directMessagesClosedError(guild, interviewee)
            }

        privateChannel.sendMessage(EmbedService.buildInterviewInstructionEmbed(configuration.prefix,
                    guild.jda.selfUser.effectiveAvatarUrl)).queue()

        val tempInterview = Interview(interviewee.id, answerChannel, bio)
        interview = tempInterview
        return InterviewCreationResult.Success(tempInterview)
    }

    fun stopInterview(): Boolean {
        interview ?: return false

        interview = null
        questionQueue.clear()

        return true
    }

    fun queueQuestionForReview(question: Question, guild: Guild) {
        val guildConfiguration = configuration.getGuildConfig(guild.id) ?: return
        val reviewChannel = guild.getTextChannelById(guildConfiguration.reviewChannelId) ?: return

        reviewChannel.sendMessage(embedService.buildQuestionReviewEmbed(question)).queue {
            questionReviewStore[it.id] = question

            it.addReaction("\u2705").queue()
            it.addReaction("\u274C").queue()
        }
    }

    fun processReviewEvent(channel: TextChannel, messageId: String, approved: Boolean) {
        val question = questionReviewStore[messageId] ?: return

        if (question in questionQueue) return

        if (approved)
            questionQueue.add(question)

        println("Question added into Queue: $question")

        channel.retrieveMessageById(messageId).queue {
            channel.editMessageById(it.id, it.embeds.first().toEmbedBuilder()
                .setColor(if (approved) Color.GREEN else Color.RED).build()).queue()
        }
    }
}

sealed class InterviewCreationResult {
    data class Success(val interview: Interview) : InterviewCreationResult() {
        companion object
    }

    data class Error(val message: String) : InterviewCreationResult() {
        companion object
    }
}
