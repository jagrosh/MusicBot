FROM openjdk:10

COPY entrypoint.sh /

RUN chmod +x /entrypoint.sh

VOLUME /bot

VOLUME /config

WORKDIR /config

ENTRYPOINT [ "/entrypoint.sh" ]
