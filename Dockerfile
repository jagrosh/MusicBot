FROM amazoncorretto:11-al2023

ARG MUSICBOT_VERSION

WORKDIR /

RUN pwd
RUN ls -lrt

ENV TOKEN harsh

RUN echo "Building MusicBot version: $MUSICBOT_VERSION"

# Download and rename the JAR file based on the provided version
RUN curl -LJO "https://github.com/jagrosh/MusicBot/releases/download/$MUSICBOT_VERSION/JMusicBot-$MUSICBOT_VERSION.jar" \
    && mv "JMusicBot-$MUSICBOT_VERSION.jar" JMusicBot.jar

# Copying Repo config.txt file to Build
COPY config.txt /config.txt

# Replace occurrences of ${BOT-TOKEN} with the value of TOKEN in config.txt during runtime
CMD ["sh", "-c", "sed -i 's/${BOT-TOKEN}/${TOKEN}/g' /config.txt && java -Dnogui=true -jar /JMusicBot.jar"]
