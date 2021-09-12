FROM alpine:3

RUN apk update \
 && apk add sudo \
 && apk add openjdk11-jre \
 && addgroup -g 1000 --system musicbot \
 && adduser -u 1000 --system --no-create-home musicbot --ingroup musicbot \
 && mkdir /bot \
 && ln -s /bot-data/serversettings.json /bot/serversettings.json

COPY start.sh /script/

ARG VERSION
COPY JMusicBot-${VERSION}.jar /bot/JMusicBot.jar

CMD /script/start.sh
