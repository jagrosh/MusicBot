FROM maven:3.6.3-jdk-8 as builder
WORKDIR /build
# Coping the pom file
COPY pom.xml .
# Download depedencies for cache
RUN mvn org.apache.maven.plugins:maven-dependency-plugin:3.1.1:go-offline -B
# Go offline command doesn't download this artifact, so i am downloading manually
RUN mvn org.apache.maven.plugins:maven-dependency-plugin:3.1.1:get \
        -DrepoUrl=https://jcenter.bintray.com/ \
        -Dartifact=org.apache.maven.surefire:surefire-junit4:2.12.4
# Coping source files
COPY src/ /build/src
# Building the project
RUN mvn -o -B package

FROM adoptopenjdk:8-jre-hotspot
WORKDIR /usr/app
# If set to 0, disable config.txt writes
ENV CONFIG_ENABLE 1
# Coping the builded jars from the previous stage
COPY --from=builder /build/target/JMusicBot-*-All.jar .
# Declaring volume
VOLUME /MusicBot
# Creating symlink of configuration to the volume, because i don't want to include the jars in the volume
RUN ln -s /MusicBot/serversettings.json /usr/app/serversettings.json
# Coping config
COPY .docker/env.conf /usr/app/config.txt
# Coping entrypoint
COPY .docker/entrypoint.sh /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]