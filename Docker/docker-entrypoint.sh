#!/bin/sh

echo "Starting JMusicBot by Jagrosh https://github.com/jagrosh/MusicBot"
echo "Containerized by Craumix https://github.com/Craumix/jmb-container"
echo "Version: $JMB_VERSION"

cd /jmb/config
java -jar -Dnogui=true /jmb/JMusicBot.jar

echo "Seems like java stoped... Waiting for 30 seconds before termination..."
sleep 30s
echo "Terminating!"
