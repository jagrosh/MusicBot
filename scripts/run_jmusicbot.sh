#!/usr/bin/env bash

set -euo pipefail

# This will have this script check for a new version of JMusicBot every
# startup (and download it if the latest version isn't currently downloaded)
: "${DOWNLOAD=true}"

# This will cause the script to run in a loop so that the bot auto-restarts
# when you use the shutdown command
: "${LOOP:=true}"

########################
### HELPER FUNCTIONS ###
########################

is_truthy() {
  # Case-insensitive check whether given variable contains one of the "truthy" words
  local -r varname="${1?Missing var name}"
  case "${!varname,,}" in
    yes|true|1) return 0;;
    no|false|0) return 1;;
    *) printf 'Variable %s is invalid. Expected true/false, got "%s". Aborting.\n' "${varname}" "${!varname}" >&2; exit 1;;
  esac
}

download() {
  is_truthy DOWNLOAD || return 0
  URL=$(curl -s https://api.github.com/repos/jagrosh/MusicBot/releases/latest \
     | grep -i 'browser_download_url.*\.jar' \
     | sed 's/.*\(http.*\)"/\1/')
  FILENAME=$(<<<"${URL}" sed 's/.*\/\([^\/]*\)/\1/')
  if [ -f "$FILENAME" ]; then
    echo "Latest version already downloaded (${FILENAME})"
  else
    curl -L "$URL" -o "$FILENAME"
  fi
}

run() {
  java -Dnogui=true "${@}" -jar "$(ls -t JMusicBot* | head -1)"
}

#######################
### PARSE ARGUMENTS ###
#######################

while [ "${#}" -gt 0 ]; do
  arg="${1}"
  shift
  if [ "${arg:0:2}" != "--" ]; then
    printf "Not a valid argument: %s. If you want to pass arguments to jvm, use '--' argument before them\n" "${arg}" >&2
    exit 1
  fi
  if [ "${arg}" == '--' ]; then break; fi # Rest are jvm flags
  set -x
  arg="${arg:2}" # Get rid of `--` prefix
  if [ "${arg:0:3}" == "no-" ]; then
    # This is a disabling flag like --no-loop or --no-download
    truthiness='false'
    arg="${arg:3}" # get rid of no- prefix
  else
    truthiness='true'
  fi
  case "${arg,,}" in
  download|loop) declare "${arg^^}"="${truthiness}";;
    *) printf "Invalid argument: '--%s'\n" "${arg}" >&2; exit 1;;
  esac
done

############
### MAIN ###
############

if is_truthy LOOP; then
  while download && run "${@}"; do
    continue
  done
else
  download && run "${@}"
fi
