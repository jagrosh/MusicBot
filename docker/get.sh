#!/bin/bash
echo "$VERSION"
[ "${VERSION}" == "Latest" ] && \
VERSION=$(curl -s https://api.github.com/repos/jagrosh/MusicBot/releases/latest \
| grep "tag_name" \
| awk '{print substr($2, 2, length($2)-3)}')
curl -L -o /jmb/JMusicBot.jar "https://github.com/jagrosh/MusicBot/releases/download/${VERSION}/JMusicBot-${VERSION}.jar" || exit 1