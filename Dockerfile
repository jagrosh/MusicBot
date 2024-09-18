# ---------------------------------------------
# Build JDA-Utilities
FROM maven:3.8.6-openjdk-11 AS jda-builder

WORKDIR /app

RUN git clone https://github.com/JDA-Applications/JDA-Utilities ./ \
	&& git checkout c16a4b264b7dc492b35e65cb295aec9980d186b2 \
    && chmod +x ./gradlew \
    && ./gradlew clean \
    && ./gradlew build \
    && ./gradlew publishToMavenLocal

# ---------------------------------------------
# Build JMusicBot
FROM maven:3.8.5-openjdk-17 AS builder

WORKDIR /app
ADD . .

COPY --from=jda-builder /root/.m2/repository /root/.m2/repository
COPY --from=jda-builder /app/build/libs /root/.m2/repository/com/jagrosh/jda-utilities/3.1.0

RUN mvn clean
RUN mvn install

# ---------------------------------------------
# Create final image with only runtime dependencies
FROM debian:12.7-slim

WORKDIR /app

COPY --from=builder /app/target/JMusicBot-Snapshot-All.jar /app/JMusicBot.jar

RUN apt-get update \
    && apt-get install -y openjdk-17-jre-headless \
    && apt-get install -y locales \
    && rm -rf /var/lib/apt/lists/* \
    && localedef -i en_US -c -f UTF-8 -A /usr/share/locale/locale.alias en_US.UTF-8

ENV LANG en_US.utf8

CMD [ "/usr/bin/java", "-Dnogui=true", "-Dconfig.override_with_env_vars=true", "-jar", "/app/JMusicBot.jar" ]
