FROM openjdk:8

LABEL version="Latest"

# Empty version will build for latest
ARG VERSION=""

COPY get.sh /

RUN VERSION="${VERSION}" ./get.sh \
&& rm /get.sh

VOLUME /config

WORKDIR /config

CMD [ "java", "-jar", "-Dnogui=true", "/JMusicBot.jar" ]
