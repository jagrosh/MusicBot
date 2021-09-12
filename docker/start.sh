#!/bin/sh

config="config.txt"

cd /bot

printf 'playlistsfolder = "/bot-data/playlists"\n\n' > $config

if [ -e "/bot-data/config.txt" ]; then
    cat "/bot-data/config.txt" >> $config
elif [ -e "/config/config/txt" ]; then
    cat "/config/config/txt" >> $config
fi

printf '\n\n' >> $config

for envvar in $(awk "END { for (envvar in ENVIRON) { print envvar; }}" < /dev/null)
do
  if [ ! "$envvar" = "${envvar#MB-}" ]; then
    value="$(awk "END { printf ENVIRON[\"$envvar\"]; }" < /dev/null)"
    printf "${envvar#MB-} = $value\n\n" >> $config
  fi
done

if [ ! -d "/bot-data" ]; then
    mkdir /bot-data
fi

chown -R musicbot:musicbot .
chown -R musicbot:musicbot /bot-data

sudo -u musicbot java -Dnogui=true -jar JMusicBot.jar
