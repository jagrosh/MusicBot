.PHONY: build run stop logs test clean rebuild shell help

COMPOSE_FILE ?= compose.yaml

help:
	@echo "Usage: make <target>"
	@echo ""
	@echo "Targets:"
	@echo "  build    Build the Docker image (via Compose)"
	@echo "  run      Start the bot in the background (docker compose up -d)"
	@echo "  stop     Stop the bot (docker compose down)"
	@echo "  logs     Tail the bot logs (docker compose logs -f)"
	@echo "  restart  Rebuild and restart the bot"
	@echo "  shell    Open a shell in the running container"
	@echo "  test     Run tests (via Gradle)"
	@echo "  clean    Remove build artifacts (via Gradle)"
	@echo ""
	@echo "Variables:"
	@echo "  COMPOSE_FILE  Docker Compose file to use (default: compose.yaml)"

build:
	docker compose -f $(COMPOSE_FILE) build

run:
	docker compose -f $(COMPOSE_FILE) up -d

stop:
	docker compose -f $(COMPOSE_FILE) down

logs:
	docker compose -f $(COMPOSE_FILE) logs -f

restart: build run

shell:
	docker compose -f $(COMPOSE_FILE) exec lmusicbot sh

test:
	./gradlew test

clean:
	./gradlew clean
