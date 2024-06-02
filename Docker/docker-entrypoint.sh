#!/bin/sh

# Default UID and GID if not provided
PUID=${PUID:-$DEFAULT_UID}
PGID=${PGID:-$DEFAULT_GID}

groupadd -g ${PGID} musicbot
useradd -Ms /bin/bash -u ${PUID} -g musicbot musicbot

chown -R musicbot:musicbot /jmb

echo "Starting JMusicBot by Jagrosh https://github.com/jagrosh/MusicBot"
echo "Containerized by Craumix https://github.com/Craumix/jmb-container"
echo "Version: $JMB_VERSION"

exec gosu musicbot java -jar -Dconfig=/jmb/config/config.txt -Dnogui=true /jmb/JMusicBot.jar

echo "Seems like java stoped... Waiting for 30 seconds before termination..."
sleep 30s
echo "Terminating!"