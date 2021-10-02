---
title: Advanced Playlist Generation
description: "Playlist generation snippets for advanced JMusicBot users"
---

This page describes how to generate playlist (txt) files from folders. These examples assume that your songs are in mp3 format; if you are using a different format, replace `mp3` with your format in all provided commands.

## Windows (Desktop)
1. Open Powershell in the folder with your songs (you can Shift-Right-Click and select Open Powershell Window Here from within an Explorer window)
2. Run **`Get-ChildItem . -Filter *.mp3 -Recurse | % { $_.FullName } | out-file -encoding ASCII songs.txt`**
3. Move songs.txt to your Playlists folder and rename it to whatever you want the playlist to be called.
4. Edit songs.txt if you want to add additional songs or any other comments

## Linux (Command Line)
1. Navigate to the folder with yours songs (`cd /path/to/the/folder`)
2. Run **`find "$(pwd)" | grep ".mp3" > songs.txt`**
3. Move `songs.txt` to your Playlists folder and rename it (`mv songs.txt /path/to/Playlists/playlistname.txt`)
