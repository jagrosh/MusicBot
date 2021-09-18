---
title: Advanced Eval Scripts
description: "Eval scripts for advanced JMusicBot users"
---

!!! danger
    Please remember that the eval command is potentially dangerous; it is locked to the bot owner, but _never_ run a script unless you know what it does!

The following are examples of scripts that can be run via the eval command (bot owner only). If your bot's prefix is set to `!!` and you've enabled the eval command, an eval (such as the first one listed below) is run like `!!eval jda.guilds`

### List all servers that the bot is on
```js
jda.guilds
```

### Leave a specific server
```js
jda.getGuildById("GUILDIDHERE").leave().queue()
```