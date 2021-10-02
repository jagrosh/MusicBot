---
title: Setup
description: "Setup JMusicBot"
---

## 1️⃣ Install Java
* Instructions on how to install Java on your system: [Installing Java](installing-java.md)

## 2️⃣ Download JMusicBot
* Download the latest **JMusicBot-X.Y.Z.jar** (and optionally, example **config.txt** file) from the [releases](https://github.com/jagrosh/MusicBot/releases/latest) page (or, get the URL from the releases page and then use wget or similar command-line tool to download).
* Your folder should look similar to this (on desktop):  
![View](/assets/images/folder-view.png)
!!! note
    The above image is Windows, but it should look similar on all platforms  
!!! warning
    Do not put this in the *Downloads* or *Desktop*. Use a folder within *Documents*

## 3️⃣ Edit the config file
* Fill in the config file (if you downloaded it). If you didn't download it, you will be prompted when you run for the first time. An example `config.txt` is provided below (See [Getting a Bot Token](getting-a-bot-token.md) and [Finding Your User ID](finding-your-user-id.md) if you need help with the config).  
```
token = MJHJkljflksdjfCoolTokenDudeILikeItkasdk
owner = 113156185389092864
prefix = "!"
```
!!! example
    You can also copy & paste a template from the [Example Config](config.md)

## 4️⃣ Run JMusicBot
* Run the jar file (choose one of these options):
  * Double-click the jar file (on desktop environments), OR
  * Run `java -Dnogui=true -jar JMusicBot-X.Y.Z.jar` from the command line (replace X, Y, and Z with the release numbers), OR
  * Run `nohup java -Dnogui=true -jar JMusicBot-X.Y.Z.jar &` to run in the background (Linux only)
* Provide the requested information, if prompted.
* Wait for the "Finished Loading" message.

## 5️⃣ Add your bot to your server
* When the bot starts, if it hasn't been added to any servers yet, it will provide you with a link in the console.
* Alternatively, follow these instructions (with images): [Adding Your Bot To Your Server](adding-your-bot.md)