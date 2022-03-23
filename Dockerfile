FROM java:8-jdk-alpine

WORKDIR /musicbot

VOLUME ["/musicbot/config"]

COPY target/JMusicBot-Snapshot-All.jar JMusicBot-Snapshot-All.jar

CMD ["java" "-Dconfig=/musicbot/config/config.txt" "-Dnogui=true" "-jar" "JMusicBot-Snapshot-All"]
