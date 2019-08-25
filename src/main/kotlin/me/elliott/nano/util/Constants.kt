package me.elliott.nano.util

class Constants {
    companion object {
        const val INTERVIEWEE = "Interviewee"

        //Error messages
        const val MISSING_GUILD_CONFIG = "This guild is not configured for use."
        const val MISSING_CATEGORY_CONFIG = "Could not find the configured category."
        const val MISSING_PARTICIPANT_CONFIG = "Could not find the configured participant channel."
        const val DM_CLOSED_ERROR = "**Error:** User's DMs are not open. " +
                "Please instruct them to allow DMs and try again."
    }
}