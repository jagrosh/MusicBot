FROM openjdk:8-jre-alpine

LABEL version="0.3.1"

ENV JMB_VERSION=0.3.1

RUN mkdir /jmb

ADD https://github.com/jagrosh/MusicBot/releases/download/$JMB_VERSION/JMusicBot-$JMB_VERSION.jar /jmb/JMusicBot.jar

VOLUME /jmb/config

WORKDIR /jmb/config

CMD java -jar -Dnogui=true ../JMusicBot.jar
