package me.elliott.nano.commands

import me.aberrantfox.kjdautils.api.dsl.CommandSet
import me.aberrantfox.kjdautils.api.dsl.commands
import me.aberrantfox.kjdautils.extensions.jda.fullName
import me.aberrantfox.kjdautils.internal.command.arguments.SentenceArg
import me.aberrantfox.kjdautils.internal.command.arguments.TextChannelArg
import me.aberrantfox.kjdautils.internal.command.arguments.UserArg
import me.aberrantfox.kjdautils.internal.di.PersistenceService
import me.elliott.nano.data.Configuration
import me.elliott.nano.services.InterviewService
import me.elliott.nano.util.EmbedUtils
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User

@CommandSet("Interview")
fun interviewCommands(interviewService: InterviewService, configuration: Configuration,
                      persistenceService: PersistenceService) = commands {

    command("SetInterviewee") {
        requiresGuild = false
        description = "Set the user to be interviewed."
        expect(UserArg)
        execute {
            val user = it.args.component1() as User
            interviewService.setInterviewee(user)
            return@execute it.unsafeRespond("**Success:** ${user.name} set as the interviewee.")
        }
    }

    command("SetAnswerChannel") {
        requiresGuild = false
        description = "Set the channel that the question and answer pairings will appear in."
        expect(TextChannelArg)
        execute {
            val answerChannel = it.args.component1() as TextChannel
            interviewService.setAnswerChannel(answerChannel)
            return@execute it.unsafeRespond("**Success:** Question and answer pairings will be sent to: **${answerChannel.asMention}**.")
        }
    }

    command("SetBio") {
        requiresGuild = false
        description = "Set the bio of the user that's providing answers in the AMA."
        expect(SentenceArg)
        execute {
            val bio = it.args.component1() as String
            interviewService.setBio(bio)
            return@execute it.respond("**Success:** Bio set.")
        }
    }

    command("StartInterview") {
        requiresGuild = true
        description = "Starts the interview"
        execute {
            if (interviewService.hasInterviewee && interviewService.hasAnswerChannel) {
                interviewService.startInterview(it.guild!!)
                it.respond("Interview Started!")
            } else {
                it.respond(EmbedUtils.buildNotCompleteEmbed())
            }
        }
    }
}
