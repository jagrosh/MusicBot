FROM maven:3.9.6-amazoncorretto-11

WORKDIR /app

# It would be better to build the codebase and package new docker images.
# TODO: Consider this approach and either delete this line or the maven lines
COPY scripts/run_jmusicbot.sh /app/run_jmusicbot.sh

COPY . .

# TODO: We should include the version here, but today it's based off of github actions.
# Getting errors on build due to missing dependencies
#RUN mvn --batch-mode --update-snapshots verify && mv target/*-All.jar JMusicBot-latest.jar

ENTRYPOINT ["/app/scripts/run_jmusicbot.sh"]