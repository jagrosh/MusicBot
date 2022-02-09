<img align="right" src="https://i.imgur.com/zrE80HY.png" height="200" width="200">

# JMusicBot

# cmorley191 Fork

## Extra Features
- Play command works on spotify track, album or playlist links (Right click -> Share -> Copy link to playlist)

## Setup
- Get the latest release at https://github.com/cmorley191/MusicBot/releases
  - Or compile from source, see section below
- Setup a Spotify "application" at https://developer.spotify.com/dashboard
  - This is incredibly straightforward. Name it whatever you want (hopefully something useful to you).
  - Your spotify account will be linked as the "owner" of this application, but your private spotify account data won't be accessible by it AFAIK.
  - Edit the config.txt created by JMusicBot to add the app's Client ID and Secret, or (if you haven't run the bot yet) let the startup prompts of the bot program guide you.

That's it. Do the other standard setup described on the main JMusicBot repo.

## Compiling from source
The main JMusicBot repo makes this sound way harder than it actually is, and doesn't provide instructions. Here they are:
- Clone this repo.
- Install the JDK (Java SE Development Kit), which allows you to compile java programs: https://www.oracle.com/java/technologies/downloads/
  - Setup a JAVA_HOME environment variable with the path to where this is installed (e.g. C:\Program Files\Java\jdk1.8.0_201)
- Download maven - it's a zip: https://maven.apache.org/download.cgi
  - Copy the extracted contents to a folder called "maven" in the top level of this repository. (so maven's "bin" folder should be at "MusicBot\maven\bin")
- Run "buildandrun.bat" in a command window, which essentially just runs "mvn compile && mvn package && java -jar MusicBot.jar".

Again, not sure why this was made to sound so difficult in the main repo.

# JMusicBot Readme:

[![Downloads](https://img.shields.io/github/downloads/jagrosh/MusicBot/total.svg)](https://github.com/jagrosh/MusicBot/releases/latest)
[![Stars](https://img.shields.io/github/stars/jagrosh/MusicBot.svg)](https://github.com/jagrosh/MusicBot/stargazers)
[![Release](https://img.shields.io/github/release/jagrosh/MusicBot.svg)](https://github.com/jagrosh/MusicBot/releases/latest)
[![License](https://img.shields.io/github/license/jagrosh/MusicBot.svg)](https://github.com/jagrosh/MusicBot/blob/master/LICENSE)
[![Discord](https://discordapp.com/api/guilds/147698382092238848/widget.png)](https://discord.gg/0p9LSGoRLu6Pet0k)<br>
[![CircleCI](https://img.shields.io/circleci/project/github/jagrosh/MusicBot/master.svg)](https://circleci.com/gh/jagrosh/MusicBot)
[![AppVeyor](https://ci.appveyor.com/api/projects/status/gdu6nyte5psj6xfk/branch/master?svg=true)](https://ci.appveyor.com/project/jagrosh/musicbot/branch/master)
[![CodeFactor](https://www.codefactor.io/repository/github/jagrosh/musicbot/badge)](https://www.codefactor.io/repository/github/jagrosh/musicbot)

A cross-platform Discord music bot with a clean interface, and that is easy to set up and run yourself!

[![Setup](http://i.imgur.com/VvXYp5j.png)](https://jmusicbot.com/setup)

## Features
  * Easy to run (just make sure Java is installed, and run!)
  * Fast loading of songs
  * No external keys needed (besides a Discord Bot token)
  * Smooth playback
  * Server-specific setup for the "DJ" role that can moderate the music
  * Clean and beautiful menus
  * Supports many sites, including Youtube, Soundcloud, and more
  * Supports many online radio/streams
  * Supports local files
  * Playlist support (both web/youtube, and local)

## Supported sources and formats
JMusicBot supports all sources and formats supported by [lavaplayer](https://github.com/sedmelluq/lavaplayer#supported-formats):
### Sources
  * YouTube
  * SoundCloud
  * Bandcamp
  * Vimeo
  * Twitch streams
  * Local files
  * HTTP URLs
### Formats
  * MP3
  * FLAC
  * WAV
  * Matroska/WebM (AAC, Opus or Vorbis codecs)
  * MP4/M4A (AAC codec)
  * OGG streams (Opus, Vorbis and FLAC codecs)
  * AAC streams
  * Stream playlists (M3U and PLS)

## Example
![Loading Example...](https://i.imgur.com/kVtTKvS.gif)

## Setup
Please see the [Setup Page](https://jmusicbot.com/setup) to run this bot yourself!

## Questions/Suggestions/Bug Reports
**Please read the [Issues List](https://github.com/jagrosh/MusicBot/issues) before suggesting a feature**. If you have a question, need troubleshooting help, or want to brainstorm a new feature, please start a [Discussion](https://github.com/jagrosh/MusicBot/discussions). If you'd like to suggest a feature or report a reproducible bug, please open an [Issue](https://github.com/jagrosh/MusicBot/issues) on this repository. If you like this bot, be sure to add a star to the libraries that make this possible: [**JDA**](https://github.com/DV8FromTheWorld/JDA) and [**lavaplayer**](https://github.com/sedmelluq/lavaplayer)!

## Editing
This bot (and the source code here) might not be easy to edit for inexperienced programmers. The main purpose of having the source public is to show the capabilities of the libraries, to allow others to understand how the bot works, and to allow those knowledgeable about java, JDA, and Discord bot development to contribute. There are many requirements and dependencies required to edit and compile it, and there will not be support provided for people looking to make changes on their own. Instead, consider making a feature request (see the above section). If you choose to make edits, please do so in accordance with the Apache 2.0 License.
