ARG JAVA_VERSION=11

FROM maven:3.6-openjdk-$JAVA_VERSION as builder

WORKDIR /tmp/workdir

COPY pom.xml ./

RUN mvn dependency:go-offline

COPY src/ ./src/

RUN mvn package


ARG JAVA_VERSION

FROM openjdk:$JAVA_VERSION-jre

COPY --from=builder /tmp/workdir/target/JMusicBot-*-All.jar /JMusicBot.jar

CMD ["java", "-Dnogui=true", "-jar", "/JMusicBot.jar"]
