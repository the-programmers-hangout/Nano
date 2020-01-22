package me.elliott.nano.services

import me.aberrantfox.kjdautils.api.annotation.Service
import me.elliott.nano.data.Configuration
import me.elliott.nano.extensions.toEmbedBuilder
import me.elliott.nano.util.Constants
import me.elliott.nano.util.notNull
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*
import java.awt.Color
import java.util.ArrayDeque

data class Interview(
        private var intervieweeId: String,
        val answerChannel: String,
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
    private var currentQuestion: Question? = null
    private var answerMessageMap = mutableMapOf<String, String>()


    fun retrieveInterview() = interview
    fun interviewInProgress() = interview != null
    fun getCurrentQuestion(): Question? = currentQuestion
    fun getQuestionCount() = questionQueue.size
    fun peekTopFive(): List<Question> = questionQueue.toList().take(5)

    fun addAnswerToMap(privateMessageId: String, answerChannelMessageId: String) {
        answerMessageMap[privateMessageId] = answerChannelMessageId
    }

    fun swapQuestion(questionIndex: Int): String {
        var message = "Please provide a valid question ID."

        questionQueue.elementAtOrNull(questionIndex).notNull {
            questionQueue.remove(it)
            questionQueue.addFirst(it)
            message = "Done."
        }
        return message
    }

    fun getNextQuestion(): Question? {
        currentQuestion = questionQueue.poll()
        return currentQuestion
    }

    fun editAnswerChannelMessage(privateMessageId: String, updatedText: String, jda: JDA) {
        val answerChannel = jda.getTextChannelById(interview!!.answerChannel) ?: return
        val answerChannelMessageId = answerMessageMap[privateMessageId] ?: return
        answerChannel.editMessageById(answerChannelMessageId, updatedText).queue()
    }

    fun startInterview(guild: Guild, interviewee: User, bio: String): String {
        val jda = guild.jda

        val botCategory = guild.getCategoryById(configuration.categoryId)
                ?: return Constants.MISSING_CATEGORY_CONFIG

        val answerChannel = botCategory.createTextChannel(interviewee.name).complete().id

        val participantChannel = jda.getTextChannelById(configuration.participantChannelId)
                ?: return Constants.MISSING_PARTICIPANT_CONFIG

        answerMessageMap.clear()

        participantChannel.sendMessage(EmbedService.buildInterviewStartEmbed(interviewee, bio,
                configuration.questionPrefix)).queue {
            it.channel.pinMessageById(it.id).queue()
        }

        val privateChannel = interviewee.openPrivateChannel().complete()
                ?: return Constants.DM_CLOSED_ERROR.also {
                    loggingService.directMessagesClosedError(guild, interviewee)
                }

        val avatar = jda.selfUser.effectiveAvatarUrl

        privateChannel.sendMessage(EmbedService.buildInterviewInstructionEmbed(configuration.prefix, avatar)).queue()

        interview = Interview(interviewee.id, answerChannel)
        loggingService.interviewStarted(guild, interviewee)

        return "**Success:** ${interviewee.name}'s interview has started!"
    }

    fun stopInterview(): Boolean {
        interview ?: return false

        interview = null
        questionQueue.clear()

        return true
    }

    fun queueQuestionForReview(question: Question, guild: Guild, author: User) {
        val reviewChannel = guild.getTextChannelById(configuration.reviewChannelId) ?: return

        loggingService.submittedQuestion(guild, author)

        reviewChannel.sendMessage(embedService.buildQuestionReviewEmbed(question)).queue {
            questionReviewStore[it.id] = question

            it.addReaction("\u2705").queue()
            it.addReaction("\u274C").queue()
        }
    }

    fun processReviewEvent(channel: TextChannel, moderator: User, messageId: String, approved: Boolean) {
        val question = questionReviewStore[messageId] ?: return

        if (question in questionQueue) return

        val author = channel.jda.getUserById(question.authorId) ?: return

        if (approved) {
            loggingService.questionApproved(channel.guild, moderator, author)
            questionQueue.offer(question)
        } else
            loggingService.questionDenied(channel.guild, moderator, author)

        channel.retrieveMessageById(messageId).queue {
            channel.editMessageById(it.id, it.embeds.first().toEmbedBuilder()
                    .setColor(if (approved) Color.GREEN else Color.RED).build()).queue()
        }
    }
}