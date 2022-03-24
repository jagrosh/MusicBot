FROM java:8-jdk

WORKDIR /musicbot

VOLUME ["/musicbot/config/"]

COPY target/JMusicBot-Snapshot-All.jar /musicbot/JMusicBot-Snapshot-All.jar

CMD ["/usr/bin/java", "-Dconfig=/musicbot/config/config.txt", "-Dnogui=true", "-jar", "/musicbot/JMusicBot-Snapshot-All.jar"]
