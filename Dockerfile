FROM java:8-jdk

WORKDIR /musicbot

VOLUME ["/musicbot"]

COPY target/JMusicBot-Snapshot-All.jar /JMusicBot-Snapshot-All.jar

CMD ["sh", "-c", "cp -f /JMusicBot-Snapshot-All.jar /musicbot/ && /usr/bin/java -Dconfig=/musicbot/config/config.txt -Dnogui=true -jar /musicbot/JMusicBot-Snapshot-All.jar"]
