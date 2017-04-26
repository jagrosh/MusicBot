FROM maven:3-jdk-8-onbuild



CMD ["java","-jar","target/JMusicBot-0.0.3.jar","-nogui"]