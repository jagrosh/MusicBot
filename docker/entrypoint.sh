#!/bin/sh
# checks the existing file to see the running version on the system

# grabs the latest version number from github can be overridden via VERSION FLAG
[ -z "${VERSION}"  ] && \
VERSION=$(curl -s https://api.github.com/repos/jagrosh/MusicBot/releases/latest \
| grep "tag_name" \
| awk '{print substr($2, 2, length($2)-3)}')

# checks to see if running version is latest

[ -f "/bot/${VERSION}-JMusicBot.jar" ] && curl -L -o "/bot/${VERSION}-JMusicBot.jar" "https://github.com/jagrosh/MusicBot/releases/download/${VERSION}/JMusicBot-${VERSION}.jar"

java -jar -Dnogui=true "/bot/${VERSION}-JMusicBot.jar"