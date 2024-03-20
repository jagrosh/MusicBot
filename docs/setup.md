---
title: Setup
description: "Setup JMusicBot"
---

## 1️⃣ Install Java
* JMusicBot requires Java 11
* Instructions on how to install Java on your system: [Installing Java](installing-java.md)

## 2️⃣ Download JMusicBot
* Download the latest **JMusicBot-X.Y.Z.jar** (and optionally, example **config.txt** file) from the [releases](https://github.com/jagrosh/MusicBot/releases/latest) page (or, get the URL from the releases page and then use wget or similar command-line tool to download).
* Your folder should look similar to this (on desktop):  
![View](/assets/images/folder-view.png)
!!! note
    The above image is Windows, but it should look similar on all platforms  
!!! warning
    Do not put this in the *Downloads* or *Desktop*. Use a folder within *Documents*

## 3️⃣ Configure the bot
* Create a bot account and configure it on the Discord Developer page
  * See [Getting a Bot Token](getting-a-bot-token.md) for step-by-step instructions
  * Make sure that 'Public Bot' is unchecked, and 'Message Content Intent' and 'Server Members Intent' are checked
* Create the config file
  * Running the bot without a config file will prompt you for a bot token and a user ID. After you provide these, it will generate a config file for you.
  * An example `config.txt` file can be found on [Example Config](config.md). You can create a `config.txt` file in the same folder as the bot, paste the contents of the example config file, and modify the values in it.
  * See [Getting a Bot Token](getting-a-bot-token.md) and [Finding Your User ID](finding-your-user-id.md) if you need help with finding some values for the config.

!!! warning
    You must restart the bot every time you edit `config.txt`. It is recommended to fully shut down the bot before editing the file.

## 4️⃣ Run JMusicBot
* Run the jar file (choose one of these options):
  * Double-click the jar file (on desktop environments), OR
  * Run `java -Dnogui=true -jar JMusicBot-X.Y.Z.jar` from the command line (replace X, Y, and Z with the release numbers)
* Provide the requested information, if prompted.
* Wait for the "Finished Loading" message.

!!! tip
    If you want to run the bot in the background, see [Running as a Service](running-as-a-service.md)

## 5️⃣ Add your bot to your server
* When the bot starts, if it hasn't been added to any servers yet, it will provide you with a link in the console.
* Alternatively, follow these instructions (with images): [Adding Your Bot To Your Server](adding-your-bot.md)

!!! tip
    If you run into problems, make sure to check out the [Troubleshooting](troubleshooting.md) page!