# Musicbot
### Upstream Repo - https://github.com/jagrosh/MusicBot/
##### This bot is built with Docker. I took the forked release and modified it so it no longer messages about new versions available until the original bot has a new verison available.
##### I also modified the Docker container so it runs as a non-root user. The user has a UID of 3002 and matches a user on the host system. 

##### Using Eclipse-Temurin base image.

##### I have no idea what I am doing, so use at your own risk.

`docker pull clifford64/musicbot:latest`