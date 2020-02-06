#!/bin/bash
SERVERSETTINGS=/MusicBot/serversettings.json
PROVIDED_CONFIG=/MusicBot/config.txt

# Config to be used in the bot
CONFIG=/usr/app/config.txt

cd /usr/app
# Setting configs
if [ "$CONFIG_ENABLE" == "1" ];then
  # Using provided config using env variables
  echo "Loading config from env variables"
else
  CONFIG=$PROVIDED_CONFIG
  echo "Loading config from $PROVIDED_CONFIG"
fi

# Check if serversettings exists, if not creates it
if [ ! -f "$SERVERSETTINGS" ]; then
  echo "{}" >> $SERVERSETTINGS
fi

# Starts the bot
java -Dnogui=true -Dconfig="$CONFIG" -jar JMusicBot-*-All.jar