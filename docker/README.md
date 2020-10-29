### Docker
___
**Overview**
Docker is a very useful to for creating repeatable images that can be used by other individuals.

##### Supported Flags

As this bot has a simple setup all that is needed is to mount a Directory. Place in the configuration file and Playlists folder and it will work just like the normal usual bot.  

**Directory to mount:**
* `-v '/path/to/host/':'/jmb/config':'rw'` 

**Name of docker image:**
* `--name='musicbot'`

**Name of docker network:** 
Do not change unless you know what you are doing.
* `--net='bridge'`

**Image Name:**
* `'PENDING_USERNAME_CHANGE/jmusicbot'`

**Example**

    sudo docker run -d \
    --name='<name_of_choice>' \
    --net='bridge' \
    -v '/home/<USERNAME_HOST>/musicbot_data':'/jmb/config':'rw' \
    'PENDING_USERNAME_CHANGE/jmusicbot'

#### Other Commands
Other useful useful docker commands.

**logs**
`sudo docker logs <name_of_choice>`

**Start/Stop docker**
`sudo docker start/stop <name_of_choice>`

**remove installed docker**
`sudo docker rm <name_of_choice>`

**Rmove unused images/networks**
**Use with caution!!!**
`sudo docker system prune`