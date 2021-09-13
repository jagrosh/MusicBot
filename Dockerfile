# syntax=docker/dockerfile:1.3

FROM maven:3.6.1-jdk-8-alpine as build

ENV HOME=/build
ENV MAVEN_HOME=/root/.m2/
WORKDIR $HOME

# --mount=type=cache,target=/root/.m2
COPY repository $MAVEN_HOME/repository

COPY pom.xml .
RUN mvn dependency:go-offline
VOLUME /root/.m2

COPY . .
RUN mvn package

FROM openjdk:18-alpine3.13

COPY --from=build /build/target/JMusicBot-Snapshot-All.jar JMusicBot.jar

CMD ["java", "-Dnogui=true", "-jar", "JMusicBot.jar"]
