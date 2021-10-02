---
title: Playlists
description: "Playlists on JMusicBot"
---

## 📃 Youtube Playlists
To play a youtube playlist, all you need is the `play` command and a playlist link or playlist ID.

Examples:
```diff
# Full Playlist URL
+ !play https://www.youtube.com/playlist?list=PLUib4KwT0DMJaPgg_nr1ia8FY5JcXksvb

# Playlist ID
+ !play PLUib4KwT0DMJaPgg_nr1ia8FY5JcXksvb

# Not a Playlist link! (notice the `watch?v=`)
- !play https://www.youtube.com/watch?v=bd2B6SjMh_w&list=PLUib4KwT0DMJaPgg_nr1ia8FY5JcXksvb&index=4
```


## 📃 Local Playlists
Local playlists are .txt files found in the `Playlists` folder in the same folder as you are running the bot from. Each line of the file is a new entry, and entries can be:
* links to youtube videos, soundcloud tracks, or online files
* full or relative path to local files
* links to youtube or soundcloud playlists
* links to online streams or radio
* searches, prefixed by `ytsearch:` for a youtube search and `scsearch:` for a soundcloud search

Lines starting with `#` or `//` are ignored for songs. To make a playlist automatically shuffle when loaded, add `#shuffle` or `//shuffle` on its own line somewhere in the playlist.

Example Playlist:
```
# This is an example playlist
# You can put this in your Playlists folder as example_playlist.txt

# The following line currently makes the playlist shuffle
# Remove this line entirely if you don't want shuffling
# shuffle

# youtube playlist id:
PLUib4KwT0DMJaPgg_nr1ia8FY5JcXksvb

# searches
ytsearch:gorillaz dare audio
scsearch:lights metrognome

# direct link
https://www.youtube.com/watch?v=x7ogV49WGco
```
Example Command:
```
!play playlist example_playlist
```


## 📃 Soundcloud Playlists
Just use the `play` command followed by the playlist link.

Example:
```
!play [link coming soon]
```


## 📃 Spotify
Unfortunately, Spotify's Terms of Service prevent playing music from Spotify. Please consider using a playlist converter such as [PlaylistConverter.net](http://www.playlist-converter.net/) to convert a Spotify playlist into a YouTube playlist.