ARG JAVA_VERSION=26

FROM gradle:9-jdk${JAVA_VERSION} AS build
WORKDIR /build

COPY build.gradle.kts settings.gradle.kts ./
RUN gradle --no-daemon dependencies

COPY src ./src
RUN gradle --no-daemon shadowJar

FROM eclipse-temurin:${JAVA_VERSION}-jre
WORKDIR /app

RUN groupadd -r lmusicbot && \
    useradd -r -g lmusicbot -d /app -s /sbin/nologin lmusicbot && \
    mkdir -p /app/config && \
    chown -R lmusicbot:lmusicbot /app

COPY --from=build --chown=lmusicbot:lmusicbot /build/build/libs/*-All.jar app.jar

VOLUME ["/app/config"]
USER lmusicbot

ENV JAVA_TOOL_OPTIONS="-Xms128m -Xmx512m -XX:+ExitOnOutOfMemoryError -Djava.awt.headless=true"

STOPSIGNAL SIGTERM

ENTRYPOINT ["java", "-jar", "app.jar", "--nogui"]

LABEL org.opencontainers.image.title="LMusicBot" \
       org.opencontainers.image.description="A modern Discord music bot with slash commands, Spotify, and Docker support" \
       org.opencontainers.image.source="https://github.com/Lukas48452/MusicBot"
