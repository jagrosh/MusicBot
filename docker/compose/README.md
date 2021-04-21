### Docker Compose
___
Docker compose is recomended for ease of use as the config file makes debugging easy. Simply install docker, docker-compose before getting started. 

#### Notice
These instructions are made for linux. Windows varriants do exists and are in likeness to these commands and configs but may vary.

##### Installing Docker / Compose

**Linux:**
`sudo apt install docker docker-compose`

**Windows:**
https://docs.docker.com/docker-for-windows/install/


#### Setup
Use the wiki  and edit the example-config.txt and save as config.txt make sure to delete the example when done. Make sure you are in the directory of docker-compose.yml and run.

`sudo docker-compose up -d`

##### useful commands:
**Logs**
`sudo docker-compose logs`

**Stop / Start services**
`sudo docker-compose stop / start <service_name>`

**list containers**
`sudo docker-compose ps`
