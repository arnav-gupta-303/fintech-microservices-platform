#!/bin/bash

set -e

echo "Starting deployment"

echo "Updating packages"
sudo apt update -y

echo "Installing dependencies"
sudo apt install -y docker.io docker-compose-v2 git curl

echo "Starting Docker"
sudo systemctl enable docker
sudo systemctl start docker

echo "Adding docker permissions"
sudo usermod -aG docker $USER || true

echo "Adding execute permission"
chmod +x postgres/init/init-multiple-dbs.sh || true

echo "Stopping old containers"
sudo docker compose down || true

echo "Cleaning old images"
sudo docker image prune -f

echo "Building containers"
sudo docker compose up -d --build

echo "Running containers"
sudo docker ps

echo "Deployment completed"