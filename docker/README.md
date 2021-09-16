# Docker

## Steps to run the application on a local Docker Swarm
```bash
### Needs to be done once to setup Docker Swarm
# Init Docker Swarm locally
$ docker swarm init
# Create web network to be used by traefik
$ docker network create -d overlay web

### Full flow when starting from scratch
# build applications
$ mvn clean install
# Go to Docker directory
$ cd docker
# Prepare docker environment (will copy dependencies build and more)
$ ./prepare.sh
# Build the containers
$ docker-compose build
# Process docker compose config to remove variables in it
$ docker-compose config > docker-compose.expanded.yaml
# Deploy environment e.g.
$ docker stack deploy -c docker-compose.expanded.yaml -c docker-compose.traefik.yaml search
# Get to it using http://localhost or http://127.0.0.1.
```
