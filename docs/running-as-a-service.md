---
title: Running as a service
description: "How to run JMusicBot as a service, so that it can run in the background without needing to be manually started."
---

## Running as a service
Running JMusicBot as a service allows it to run in the background without needing to be manually started. This is useful for running the bot on a server, or if you want to be able to close your terminal without stopping the bot.

### Linux using systemd

!!! warning
    This method assumes that you've created a user for the bot to run as. If you haven't, see [this guide](https://www.digitalocean.com/community/tutorials/how-to-create-a-sudo-user-on-ubuntu-quickstart) for instructions.

!!! note
    Copy the jar file to the home directory of the user that the bot is running as, or change the `WorkingDirectory` and `ExecStart` lines in the service file to point to the correct location.

1. Open a terminal and run the following command to create a new service file:

```bash
sudo nano /etc/systemd/system/JMusicBot.service
```

2. Copy the following text into the file and save it:

```ini
[Unit]
Description=JMusicBot
Wants=network.target
After=network.target

[Service]
WorkingDirectory=/home/<username>
User=<username>
Group=<username>
Type=simple
ExecStart=/usr/bin/env java -Dnogui=true -jar JMusicBot.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

!!! note
    Replace `<username>` with the name of the user that the bot is running as.

4. Run the following command to start the bot:

```bash
sudo systemctl start JMusicBot
```

5. Run the following command to stop the bot:

```bash
sudo systemctl stop JMusicBot
```

6. Run the following command to restart the bot:

```bash
sudo systemctl restart JMusicBot
```

7. Run the following command to enable the bot to start on boot:

```bash
sudo systemctl enable JMusicBot
```


### Linux using screen

!!! warning
    This method is not recommended for production use, see [systemd](#linux-using-systemd) instead.

1. Install the [screen](https://www.howtoforge.com/linux_screen) utility, if it isn't already installed.
2. Run the following command to start the bot:

```bash
screen -dmS JMusicBot java -jar JMusicBot.jar
```

3. Run the following command to stop the bot:

```bash
screen -S JMusicBot -X quit
```

4. Run the following command to restart the bot:

```bash
screen -S JMusicBot -X quit
screen -dmS JMusicBot java -jar JMusicBot.jar
```

### Windows

1. Download the [NSSM](https://nssm.cc/download) executable and place it in the same directory as the JMusicBot jar file.
2. Open a command prompt in the same directory as the JMusicBot jar file and run the following command:

```bat
nssm install JMusicBot java -jar JMusicBot.jar
```

3. Run the following command to start the service:

```bat
nssm start JMusicBot
```

4. Run the following command to stop the service:

```bat
nssm stop JMusicBot
```

5. Run the following command to remove the service:

```bat
nssm remove JMusicBot
```