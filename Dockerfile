FROM adoptopenjdk:8-jre-hotspot
WORKDIR /usr/app
COPY ./target/JMusicBot-*-All.jar .
ENV DISCORD_BOT_TOKEN=""
ENV DISCORD_BOT_OWNER="94934681706835968"
ENV DISCORD_BOT_PREFIX="!"
COPY .docker/entrypoint.sh /entrypoint.sh
VOLUME /usr/app/serversettings.json
ENTRYPOINT ["/entrypoint.sh"]