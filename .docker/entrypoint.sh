#!/bin/bash
serversettings=/MusicBot/serversettings.json
config=/MusicBot/config.txt
# Store a entry in the config
# $1 = Config key
# $2 = Env Variable
function env_to_config() {
  KEY="${2}"
  if [[ ! -v $KEY ]]; then
    echo "$KEY is not set, skipping"
    return
  fi
  echo "$1 = \"${!KEY}\"" >> config.txt
}
# Load a config from the env variable $2
# $1 Config key
function load_from_env(){
  KEY=${1}
  env_to_config $KEY "CONFIG_${KEY^^}"
}

function load_from_env_required(){
  KEY="CONFIG_${1^^}"
  if [[ ! -v $KEY ]]; then
    echo "$KEY is not set and is required"
    exit 1
  fi
  load_from_env "$1"
}

cd /usr/app
# Setting configs
if [ "$CONFIG_ENABLE" == "1" ];then
  echo "Loading config from env variables"
  rm -rf config.txt
  # Setting playlist folder to volume location
  echo "playlistsfolder = /MusicBot/Playlists" >> config.txt
  # Loading user env variables
  load_from_env_required token true
  load_from_env_required owner
  load_from_env prefix
  load_from_env game
  load_from_env songinstatus
  load_from_env altprefix
  load_from_env help
  load_from_env stayinchannel
  load_from_env maxtime
  load_from_env updatealerts
  load_from_env "eval"
  env_to_config "lyrics.default" CONFIG_LYRICS_PROVIDER
else
  # If not using env variables to configure the bot, create a symlink to /MusicBot/config.txt file
  if [ ! -f "$config" ]; then
      if [ -f config.txt ]; then
        # If config already exists, just copy it
        cp config.txt $config
      else
        touch $config
      fi
  fi
  # If the symlink don't exists create it
  if [ ! -L config.txt ]; then
        rm -f config.txt
        echo "Creating config symlink"
        ln -s $config /usr/app/config.txt
      fi
fi

# Check if serversettings exists, if not creates it
if [ ! -f "$serversettings" ]; then
  echo "{}" >> $serversettings
fi

# Starts the bot
java -Dnogui=true -jar JMusicBot-*-All.jar