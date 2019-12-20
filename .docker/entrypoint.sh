#!/bin/bash
cd /usr/app
rm -rf config.txt
if [ "$DISCORD_BOT_TOKEN" == "" ]; then
    echo "Please provide the DISCORD_BOT_TOKEN env variable"
    exit 1
fi
echo "token = \"${DISCORD_BOT_TOKEN}\"" >> config.txt
echo "owner = \"${DISCORD_BOT_OWNER}\"" >> config.txt
echo "prefix = \"${DISCORD_BOT_PREFIX}\"" >> config.txt
java -Dnogui=true -jar JMusicBot-*-All.jar