# Commands

## Key 
| Symbol      | Meaning                        |
| ----------- | ------------------------------ |
| (Argument)  | Argument is not required.      |

## Interview
| Commands       | Arguments                           | Description                                                         |
| -------------- | ----------------------------------- | ------------------------------------------------------------------- |
| ClearChannel   |                                     | Clear all messages from a channel.                                  |
| EditMessage    | Message to edit., New message text. | Edits the target message in the channel the command was invoked in. |
| StartInterview | Interviewee, Bio                    | Set the user to be interviewed.                                     |
| StopInterview  |                                     | Stop a currently running interview.                                 |

## Interviewee
| Commands   | Arguments | Description                                                      |
| ---------- | --------- | ---------------------------------------------------------------- |
| Count      |           | Reports how many questions are pending reply.                    |
| Next       |           | Pulls the next question off the top of the queue.                |
| Peek       |           | Looks at the next five questions in the queue.                   |
| SendTyping | On or Off | Enables or disables sending typing events to the answer channel. |
| makeNext   | Integer   | Takes the provided question ID and makes that the next question. |

## Utility
| Commands | Arguments | Description          |
| -------- | --------- | -------------------- |
| Help     | (Command) | Display a help menu. |

