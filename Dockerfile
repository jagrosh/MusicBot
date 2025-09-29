# Use an OpenJDK 11 image as the base image
FROM openjdk:11-jre-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file and the config file into the container
COPY MusicBot.jar /app/MusicBot.jar
COPY config.txt /app/config.txt

# Expose any port needed by the bot (optional, replace 8080 if required)
# EXPOSE 8080

# Run the JAR file with the -Dnogui=true argument
CMD ["java", "-Dnogui=true", "-jar", "/app/MusicBot.jar", "config.txt"]
