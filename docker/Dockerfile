FROM openjdk:8

LABEL version="Latest"

# Empty version will build for latest
ARG VERSION=""

COPY docker_build.sh /

RUN VERSION="${VERSION}" ./docker_build.sh \
&& rm /docker_build.sh

VOLUME /config

WORKDIR /config

CMD [ "java", "-jar", "-Dnogui=true", "/JMusicBot.jar" ]
