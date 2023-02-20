<img align="right" src="https://i.imgur.com/zrE80HY.png" height="200" width="200">

# JMusicBot (cmorley191 Fork)

A version of [jagrosh/MusicBot](https://github.com/jagrosh/MusicBot) -- a reliable, user-friendly Discord music bot alternative to out-of-service bots like [Rythm](https://musically.com/2021/09/13/youtube-shuts-down-music-bot-rythm/) and [Groovy](https://groovy.bot/).

This version of JMusicBot mainly adds Spotify support. There are also some other extra features. The latest features of the original JMusicBot are supported as the bot is updated.

## Setup
MusicBot is not a service; you will need to download the bot application and run it yourself. Fortunately, it can take as little as 15 minutes to set up:

- Get the latest release at https://github.com/cmorley191/MusicBot/releases
  - Or compile from the source code; see section below
- Complete [the setup steps of the base JMusicBot](https://jmusicbot.com/setup/), but use the .jar file downloaded from this version. Then...
- Enable Spotify support (optional)
  - Set up a Spotify "application" at https://developer.spotify.com/dashboard
    - This is extremely straightforward; the Spotify developer page guides you through the few, easy steps.
    - Name the app whatever you want.
    - Note: your personal Spotify account will be the "owner" of this application, but your private Spotify account data won't be accessible by the app or this bot, AFAIK.
  - Input your Spotify application's Client ID and Secret into the bot's prompts as you start it up
    - These will be added to the `config.txt` file created by the bot - you can edit them there.

## Features

### Spotify Support

- Queue up a Spotify Track, Playlist, or Album by using the "play" command with a Spotify link. (right-click...Share...Copy Link)

#### _Rant from the Author_

_Given that Spotify has unparalleled features (an amazing playlist ecosystem, comprehensive artist pages, and curated playlists/"radios"), this project is pleased to offer this essential Discord music bot feature._

_The original JMusicBot project [will not add Spotify support](https://github.com/jagrosh/MusicBot/wiki/Things-That-Won%27t-Be-Added), purporting that it would be_
- _"confusing for some users" due to the need of a second API key,_
- _"unreliable at best" since it needs to search YouTube for the requested Spotify tracks, and_
- _an unuseful duplication of Discord's native [Spotify "Listening Parties"](https://support.discord.com/hc/en-us/articles/115003966072-Listening-Along-with-Spotify)_

_With cmorley191/MusicBot, you'll find that:_
- _it is **incredibly simple** to set up Spotify support within a few minutes (described in "Setup" above),_
- _JMusicBot's YouTube searches **almost never find the wrong audio** when the full song name and artist list is provided (which this bot's Spotify-based searches do automatically), and_
- _playing Spotify through this music bot has **a number of advantages over Spotify Listening Parties**:_
  - _Guild members can listen along regardless of whether they have a Spotify account._
  - _JMusicBot's vast set of features allow a better listening experience for a Discord voice channel; contrasted with a Listening Party with one person in control._
  - _Users will never desync or disconnect - issues that have been common with Listening Parties._

### Other Extra Features
- Emojis used by the bot can be configured per-guild via the interactive `setemoji` command.
  - Several emojis can be set for the bot's various usages ("success", "error", etc.).
    - The bot selects randomly from the list of emojis.
    - Weights can be added to do a weighted select - to do this, edit serversettings.json and replace the emoji strings with json objects like:
      ```{ "emoji": "<emojistring>", "weight": <weight> }```
  
      e.g.
      ```
      {{"<serverid>": {"success": [
          { "emoji": "👍", "weight": 9 },
          { "emoji": "<:customemoji:1234567890>", "weight": 2 },
          { "emoji": "❓", "weight": 7 }
      ]}}}
      ```
- Quality of life fixes:
  - The counter showing progress on loading additional tracks in a playlist now accurately shows the speed at which they are loaded.

## Compiling from source
The main JMusicBot repo makes this sound way harder than it actually is, and doesn't provide instructions. Here they are:
- Clone this repo. (https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository)
- Install the JDK (Java SE Development Kit), which allows you to compile java programs: https://www.oracle.com/java/technologies/downloads/
  - Setup a JAVA_HOME environment variable with the path to where this is installed (e.g. C:\Program Files\Java\jdk1.8.0_201)
    - Atlassian has a great 7-step tutorial for this: https://confluence.atlassian.com/doc/setting-the-java_home-variable-in-windows-8895.html
- Download maven - it's a zip file: https://maven.apache.org/download.cgi
  - Copy the extracted contents of ther zip to a folder called "maven" in the top level of this repository. (so that maven's "bin" folder should be at "MusicBot\maven\bin")
- Run "buildandrun.bat" in a command window, which essentially just runs "mvn compile && mvn package && java -jar MusicBot.jar".

Again, not sure why this was made to sound so difficult in the main repo.
