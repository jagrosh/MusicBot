FROM eclipse-temurin:22-jre-ubi9-minimal

ENV JMB_VERSION 0.4.1

#No downloadable example config since 0.2.10
RUN mkdir -p /jmb/config
ADD JMusicBot-0.4.1.jar /jmb/JMusicBot.jar
#ADD https://github.com/Amavect/MusicBot/releases/download/0.2.9/config.txt /jmb/config/config.txt

COPY ./docker-entrypoint.sh /jmb

RUN chmod +x /jmb/docker-entrypoint.sh
RUN chmod 770 /jmb/JMusicBot.jar

VOLUME /jmb/config

# Install gosu
RUN microdnf install -y shadow-utils && \
    curl -o /usr/local/bin/gosu -fsSL "https://github.com/tianon/gosu/releases/download/1.14/gosu-amd64" && \
    chmod +x /usr/local/bin/gosu

ENV DEFAULT_UID=1000
ENV DEFAULT_GID=1000

ENTRYPOINT ["/jmb/docker-entrypoint.sh"]