<picture>
  <source media="(prefers-color-scheme: dark)" srcset="https://i.imgur.com/zrE80HY.png">
  <img align="right" src="https://i.imgur.com/zrE80HY.png" height="200" width="200">
</picture>

# LMusicBot

[![License](https://shieldcn.dev/github/license/Lukas48452/MusicBot.svg?variant=secondary)](https://github.com/Lukas48452/MusicBot/blob/master/LICENSE)
[![Java](https://shieldcn.dev/badge/Java-26-orange?variant=secondary)](https://adoptium.net/temurin/releases/)
[![Gradle](https://shieldcn.dev/badge/Gradle-9.5-purple?variant=secondary)](https://gradle.org)
[![Build](https://shieldcn.dev/github/actions/workflow/status/Lukas48452/MusicBot/build-and-test.yml?variant=secondary)](https://github.com/Lukas48452/MusicBot/actions/workflows/build-and-test.yml)
[![Docker](https://shieldcn.dev/badge/Docker-Compose-blue?variant=secondary)](https://docs.docker.com/compose/)
[![Release](https://shieldcn.dev/github/v/release/Lukas48452/MusicBot.svg?variant=secondary)](https://github.com/Lukas48452/MusicBot/releases)

A **modern, actively maintained fork** of JMusicBot — a cross-platform Discord music bot with slash commands, Spotify support, webhook notifications, and optional Redis/PostgreSQL storage. Now renamed to **LMusicBot (Lukas' Music Bot)**.

---

## What's New in This Fork

| Feature | Original JMusicBot | LMusicBot |
|---------|:-:|:-:|
| Slash commands | ✗ | ✓ (14 commands) |
| Spotify tracks/playlists/albums | ✗ | ✓ |
| Interaction modes (text/slash/both) | ✗ | ✓ |
| Webhook notifications | ✗ | ✓ |
| Redis storage backend | ✗ | ✓ |
| PostgreSQL storage backend | ✗ | ✓ |
| Docker image & GHCR publishing | ✗ | ✓ |
| JDK 26 / Gradle 9.5 | ✗ | ✓ |
| Config in YAML (HOCON) | ✗ | ✓ |
| Makefile convenience commands | ✗ | ✓ |

All original features (YouTube, SoundCloud, Bandcamp, Vimeo, Twitch, local playlists, DJ roles, embed menus) are fully preserved.

---

## Features

- **14 slash commands** — `/play`, `/skip`, `/stop`, `/pause`, `/volume`, `/queue`, `/nowplaying`, `/shuffle`, `/search`, `/lyrics`, `/remove`, `/repeat`, `/forceskip`, `/playnext`
- **Interaction modes** — choose slash commands, text commands, or both
- Spotify tracks, playlists, and albums via Spotify Web API
- YouTube, SoundCloud, Bandcamp, Vimeo, Twitch, and more
- Optional **Redis** and **PostgreSQL** storage backends
- **Webhook notifications** — nowplaying and queue updates sent to a Discord webhook
- Local file and playlist support
- Clean embed-based menus
- DJ role with per-server permissions
- **Docker Compose** for one-command deployment
- **Makefile** convenience commands
- Updated dependencies — modern JDA, Lavaplayer, Logback, Typesafe Config

## Quick Start

### Docker Compose (Recommended)

```sh
git clone https://github.com/Lukas48452/MusicBot.git
cd MusicBot
make run
```

The bot restarts automatically unless stopped with `make stop` or `docker compose down`.

| Command | Description |
|---------|-------------|
| `make build` | rebuild the Docker image |
| `make run` | start the bot in the background |
| `make stop` | stop the bot |
| `make logs` | tail bot logs |
| `make restart` | rebuild and restart |
| `make test` | run tests via Gradle |

### Manual (Gradle)

```sh
./gradlew shadowJar
java -jar build/libs/LMusicBot-All.jar --nogui
```

Requirements: JDK 26+, Gradle 9.5+ (or use the bundled wrapper).

## Configuration

Create `config.yaml` in the project root or `config/` directory:

```yaml
token = "your-bot-token"
owner = 123456789012345678
prefix = "!"
interactionmode = "all"
```

### Core Settings

| Setting | Default | Description |
|---------|---------|-------------|
| `token` | `BOT_TOKEN_HERE` | Discord bot token (required) |
| `owner` | `0` | Owner user ID (required) |
| `prefix` | `@mention` | Text command prefix |
| `interactionmode` | `all` | `all`, `text`, or `slash` |

### Spotify

| Setting | Default | Description |
|---------|---------|-------------|
| `spotify.clientid` | `""` | Spotify API client ID |
| `spotify.clientsecret` | `""` | Spotify API client secret |

### Webhook Notifications

| Setting | Default | Description |
|---------|---------|-------------|
| `webhook.url` | `""` | Discord webhook URL for nowplaying/queue updates |
| `webhook.updatetime` | `5` | Update interval in seconds |

### Storage Backend

| Setting | Default | Description |
|---------|---------|-------------|
| `storage.type` | `file` | `file`, `redis`, or `postgres` |
| `storage.redis.host` | `localhost` | Redis host |
| `storage.redis.port` | `6379` | Redis port |
| `storage.redis.password` | `""` | Redis password (optional) |
| `storage.postgres.host` | `localhost` | PostgreSQL host |
| `storage.postgres.port` | `5432` | PostgreSQL port |
| `storage.postgres.database` | `lmusicbot` | PostgreSQL database name |
| `storage.postgres.user` | `lmusicbot` | PostgreSQL user |
| `storage.postgres.password` | `""` | PostgreSQL password |

### Interaction Modes

| Mode | Text Commands | Slash Commands |
|------|:---:|:---:|
| `all` (default) | ✓ | ✓ |
| `text` | ✓ | ✗ |
| `slash` | ✗ | ✓ |

## Spotify Setup

1. Go to the [Spotify Developer Dashboard](https://developer.spotify.com/dashboard) and create an app
2. Set the redirect URI to `https://example.com/callback` (not used by the bot)
3. Copy the **Client ID** and **Client Secret**
4. Add them to `config.yaml`:
   ```yaml
   spotify {
       clientid = "your-client-id"
       clientsecret = "your-client-secret"
   }
   ```
5. Paste Spotify URLs into Discord — tracks, playlists, and albums all work

## Docker Images

Published to GHCR on every release:

```sh
docker pull ghcr.io/Lukas48452/musicbot:latest
```

See [PUBLISH_GHCR.md](PUBLISH_GHCR.md) for setup instructions.

## Building from source

```sh
./gradlew build          # compile and run tests
./gradlew shadowJar      # build standalone fat JAR
./gradlew test           # run tests only
```

## Contributing

- Report bugs or suggest features via [Issues](https://github.com/Lukas48452/MusicBot/issues)
- Ask questions or get help in [Discussions](https://github.com/Lukas48452/MusicBot/discussions)
- Read the code of conduct before submitting a PR

## License

Apache 2.0 — see [LICENSE](LICENSE).

Thanks to [JDA](https://github.com/DV8FromTheWorld/JDA), [lavaplayer](https://github.com/sedmelluq/lavaplayer), [jagrosh](https://github.com/jagrosh) for the original JMusicBot, and all other open-source libraries that make this project possible.
