# Roadmap

Ideas and features being considered for future releases.

## Sources & Playback
- [ ] **Apple Music support** — resolve Apple Music URLs via their API
- [ ] **Deezer support** — resolve Deezer tracks and playlists
- [ ] **SoundCloud playlist pagination** — load full SoundCloud playlists beyond the first page
- [ ] **Podcast / RSS feed support** — play audio from podcast RSS feeds
- [ ] **Crossfade between tracks** — smooth transitions when auto-playing next song
- [ ] **Equalizer / audio filters** — bass boost, treble, etc., per-guild or per-user

## Commands & UX
- [ ] **Slash commands** — migrate to Discord's new command framework
- [ ] **Lyrics in chat** — display lyrics in real-time as the song plays (not just a one-shot command)
- [ ] **Song requests** — allow non-DJ users to request songs into a separate request queue
- [ ] **Playlist management via Discord** — create, edit, delete, and share playlists from chat
- [ ] **Reaction-based controls** — pause/skip/stop via emoji reactions on nowplaying message
- [ ] **Multi-language support** — localize bot responses (contributions welcome)

## Infrastructure
- [ ] **Web dashboard** — browser-based config editor, queue viewer, and stats
- [ ] **Healthchecks & metrics** — Prometheus metrics endpoint for monitoring
- [ ] **Kubernetes helm chart** — deploy on K8s clusters
- [ ] **Database-backed settings** — migrate per-guild settings from JSON files to a database
- [ ] **Configuration UI** — generate and edit config.yaml through an interactive CLI wizard
- [ ] **Automated builds** — publish Docker images to GHCR on each release

## Integrations
- [ ] **Last.fm scrobbling** — scrobble played tracks to Last.fm
- [ ] **Webhook notifications** — post nowplaying and queue updates to a Discord webhook
- [ ] **Spotify search command** — `!spsearch query` to search Spotify directly
- [ ] **YouTube login** — optional token for age-restricted content

## Quality of Life
- [ ] **Per-user volume** — remember individual user volume preferences
- [ ] **Auto-generated playlists** — recommend songs based on recently played
- [ ] **Queue history** — show recently played tracks with `!history`
- [ ] **Save queue** — save and restore the current queue across bot restarts

---

*Contributions are welcome! If you'd like to work on any of these items, open an issue first to discuss the approach.*
