#!/bin/sh

# Run LMusicBot via Docker Compose
# The bot restarts automatically unless stopped explicitly

COMPOSE_FILE="${1:-compose.yaml}"

while true; do
    echo "Starting LMusicBot via Docker Compose (${COMPOSE_FILE})..."
    docker compose -f "${COMPOSE_FILE}" up --build -d

    echo "LMusicBot is running. Attach logs with: docker compose logs -f"
    echo "Stop the bot with: docker compose down"
    echo "Press Ctrl+C to exit this launcher (bot keeps running)."

    # Wait indefinitely; if the container stops unexpectedly, restart it
    docker wait "$(docker compose -f "${COMPOSE_FILE}" ps -q lmusicbot 2>/dev/null)" 2>/dev/null

    echo "Container stopped. Restarting in 5 seconds..."
    sleep 5
done 
