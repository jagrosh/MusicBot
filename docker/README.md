### Docker

Docker is a very useful to for creating repeatable images that can be used by other individuals.

##### Supported Flags

As this bot has a simple setup all that is needed is to mount a Directory place in the configuration file and Playlists folder and it will work just like the normal usual bot.  

###### Directory to mount 
* -v '/path/to/host/':'/jmb/config':'rw' 

###### Name of docker image
* --name='musicbot'

###### Name of docker network 
Do not change unless you know what you are doing.
* --net='bridge'