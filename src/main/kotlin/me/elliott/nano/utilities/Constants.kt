package me.elliott.nano.utilities

class Constants {
    companion object {
        const val INTERVIEWEE_CATEGORY = "Interviewee"

        //Error messages
        const val CONFIG_NOT_SETUP = "You must first use the `Setup` command."
        const val INTERVIEW_RUNNING = "There is already an interview running."
        const val MISSING_CATEGORY_CONFIG = "Could not find the configured category."
        const val MISSING_PARTICIPANT_CONFIG = "Could not find the configured participant channel."
        const val DM_CLOSED_ERROR = "**Error:** User's DMs are not open. " +
                "Please instruct them to allow DMs and try again."
    }
}