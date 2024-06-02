# Musicbot
### Upstream Repo - https://github.com/jagrosh/MusicBot/
##### This bot is built with Docker. I took the forked release and modified it so it no longer messages about new versions available until the original bot has a new verison available.
##### I also modified the Docker container so it runs as a non-root user. The user has a UID of 3002 and matches a user on the host system.

##### Using Eclipse-Temurin base image. 

##### I have updated the original dependencies to remediate some vulnerability findings.

##### I have no idea what I am doing, so use at your own risk.

### Specify User

##### Docker image has been updated to allow for specifying the UID and GID of the account running the container. Simply specify the environment variable PUID and PGID of the account in the run command. If no variable is specified, the bot runs 1000.

`docker run -e PUID=3002 -e PGID=3002 -v /path/to/config:/jmb/config clifford64/musicbot:latest`