#!/bin/bash
# Directory to persist files
PERSIST_DIR=/MusicBot
# File to store server settings
SERVER_SETTINGS="${PERSIST_DIR}/serversettings.json"
# Config used in the bot
CONFIG_FILE="${PERSIST_DIR}/config.txt"
# Default config to be used in the bot
DEFAULT_CONFIG=/usr/app/defaultConfig.txt

cd /usr/app
# If the persist dir doesn't exist create it
if [ ! -d "${PERSIST_DIR}" ]; then
  mkdir "$PERSIST_DIR"
fi
# Check if the config doesn't exist, and if doesn't copy the default one
if [ ! -f "$CONFIG_FILE" ]; then
  echo "Copping default config file to $CONFIG_FILE"
  cp "$DEFAULT_CONFIG" "$CONFIG_FILE"
fi

# Check if serversettings exists, if not creates it
if [ ! -f "$SERVER_SETTINGS" ]; then
  echo "{}" >> "$SERVER_SETTINGS"
fi

# Starts the bot
exec java -Dnogui=true -Dconfig="$CONFIG_FILE" $JAVA_OPTS -jar JMusicBot-*-All.jar
