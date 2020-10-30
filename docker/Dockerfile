FROM openjdk:8

LABEL version="Latest"

ENV VERSION="Latest"

RUN mkdir /jmb

COPY get.sh /jmb

RUN VERSION="${VERSION}" ./jmb/get.sh \
&& rm /jmb/get.sh

VOLUME /jmb/config

WORKDIR /jmb/config

CMD [ "java", "-jar", "-Dnogui=true", "/jmb/JMusicBot.jar" ]
