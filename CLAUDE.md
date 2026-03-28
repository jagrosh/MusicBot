# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# Build fat JAR (output: target/JMusicBot-Snapshot-All.jar)
mvn clean package

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=FairQueueTest

# Run integration tests (used by CI)
mvn integration-test

# Full build + test
mvn verify

# Generate default config file
java -jar target/JMusicBot-Snapshot-All.jar generate-config

# Run the bot (disable GUI with -Dnogui=true)
java -Dnogui=true -jar target/JMusicBot-Snapshot-All.jar
```

## Architecture Overview

### Central Coordinator: `Bot.java`
`Bot` is the central hub holding references to all major subsystems: `PlayerManager`, `SettingsManager`, `PlaylistLoader`, `NowplayingHandler`, `AloneInVoiceHandler`, `EventWaiter`, and the `JDA` instance. It is passed to commands that need cross-system access.

### Entry Point & Startup: `JMusicBot.java`
Orchestrates initialization: loads `BotConfig` → creates `Bot` → registers all commands into `CommandClientBuilder` → builds JDA with event listeners. Gateway intents are manually configured (Direct Messages, Guild Messages, Guild Voice States, Guild Message Reactions).

### Command Hierarchy
All commands extend one of four abstract base classes that enforce permission tiers:

| Base Class | Permission Required |
|---|---|
| `OwnerCommand` | Bot owner only |
| `AdminCommand` | MANAGE_SERVER permission or owner |
| `DJCommand` | Guild DJ role, server manager, or owner |
| `MusicCommand` | Any user in a voice channel (optionally restricted to configured text channel) |

Each subpackage (`music/`, `dj/`, `admin/`, `owner/`, `general/`) contains concrete command implementations. New commands must be instantiated and registered in `JMusicBot.java`'s `createCommandClient()` method.

### Audio Pipeline
Lavaplayer is configured in `PlayerManager` with 10+ audio source managers (YouTube, SoundCloud, Bandcamp, Vimeo, Twitch, HTTP URLs, local files, etc.). One `AudioHandler` per guild acts as both a Lavaplayer `AudioEventAdapter` (track lifecycle) and JDA `AudioSendHandler` (frame delivery to Discord voice). Track requests flow:

```
PlayCmd → PlayerManager.loadItemOrdered() → AudioHandler.addTrack() → NowplayingHandler update
```

### Per-Guild Settings: `SettingsManager` + `Settings`
Guild settings (text channel, voice channel, DJ role, volume, default playlist, repeat mode, skip ratio, queue type) are persisted to `serversettings.json` via `SettingsManager`. Settings are cached in-memory in a `HashMap<Long, Settings>`.

### Queue Types
Two implementations of `AbstractQueue<T extends Queueable>`:
- `LinearQueue` — standard FIFO
- `FairQueue` — prevents a single user from monopolizing the queue

Queue type is configurable per guild via `QueueType` enum in Settings.

### Configuration Layers
1. **`config.txt`** (HOCON, Typesafe Config) — bot token, prefix, owner ID, global feature flags
2. **`serversettings.json`** — per-guild runtime settings managed by `SettingsManager`
3. **Playlist files** (`playlists/*.txt`) — line-separated URLs loaded by `PlaylistLoader`
