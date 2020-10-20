# Commands

## Key 
| Symbol      | Meaning                        |
| ----------- | ------------------------------ |
| (Argument)  | Argument is not required.      |

## Guild Configuration
| Commands           | Arguments      | Description                                                 |
| ------------------ | -------------- | ----------------------------------------------------------- |
| AMACategory        | Category       | Set the category where Q&A channels will be created.        |
| LoggingChannel     | Channel        | Set the channel where logs will be output.                  |
| ParticipantChannel | Channel        | Set the channel where participants can take part.           |
| Prefix             | Prefix         | Set the prefix required for the bot to register a command.  |
| QuestionPrefix     | QuestionPrefix | Set the prefix required for the bot to register a question. |
| ReviewChannel      | Channel        | Set the channel where question reviews will be output.      |
| Setup              |                | Setup a guild to use Nano                                   |
| StaffRole          | Role           | Set the role required to use this bot.                      |

## Interview
| Commands       | Arguments        | Description                         |
| -------------- | ---------------- | ----------------------------------- |
| StartInterview | Interviewee, Bio | Set the user to be interviewed.     |
| StopInterview  |                  | Stop a currently running interview. |

## Interviewee
| Commands   | Arguments | Description                                                      |
| ---------- | --------- | ---------------------------------------------------------------- |
| Answer     |           | Answers the question on top of the queue.                        |
| Count      |           | Reports how many questions are pending reply.                    |
| Later      |           | Returns current question to the back of the queue.               |
| MakeNext   | Integer   | Takes the provided question ID and makes that the next question. |
| Next       |           | Pulls the next question off the top of the queue.                |
| Peek       |           | Looks at the next five questions in the queue.                   |
| SendTyping | On or Off | Enables or disables sending typing events to the answer channel. |

## Utility
| Commands     | Arguments | Description                        |
| ------------ | --------- | ---------------------------------- |
| ClearChannel | (Channel) | Clear all messages from a channel. |
| Help         | (Command) | Display a help menu.               |

