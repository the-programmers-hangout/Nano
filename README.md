# Nano - A Minimalistic Q&A Bot. 

Nano is a community bot for running live AMA's. Interviews can be started with a target user and members of the community can ask questions. These questions are filtered by staff, and then answered by the interviewee in a DM with the bot.



#### Participant Experience 

Interview participants can ask questions in the configured channel, using the prefix you've chosen for questions. Once a question is submitted, it goes into a moderation queue channel for review. 



**Asking Questions**

![Asking Questions](https://i.imgur.com/V4gjsEA.png)



If the question gets approved by the moderation team, it gets added to the interviewee's question queue. Participants can view answers to questions in the answer channel that gets auto-generated for that interview. 

**Viewing Answers**

![Viewing Answers](https://i.imgur.com/PVpUOpu.png)



#### Interviewee Experience 

Once the interview is started the interviewee will receive a private message from the bot explaining how to answer questions. 

![Interview Start Message](https://i.imgur.com/p9P8gsg.png)



**Answering Questions**

![Answer Questions](https://i.imgur.com/CqW9AZh.png)



#### Moderation Experience 

Whichever channel you specify as the moderation queue will receive the questions that users ask and give you the opportunity to approve or reject a question. It's purely based on which reaction happens to the message first at this point. Once approved, it goes into the interviewee's queue for answering. If an interviewee doesn't want to answer the questions they can simply use the `next` command to skip it. 



![Moderation Queue](https://i.imgur.com/LlUfjh9.png)



## Commands

Refer to [Commands.md](https://gitlab.com/tphelliott/nano/blob/master/commands.md) for a general list and explanation of all available commands. To learn about commands during runtime, use the `help` command!



## Setup Guide 

Since this is such a special purpose bot, I may never get around to writing a setup guide. At this point, whenever it comes to running it, you're completely on your own. Do not DM me and ask for help. It's extremely simple to get going and you can reference other KUtils projects for examples on how to go about it. 



