#!/bin/bash

apt update

apt install -y build-essential libpq-dev

docker run --rm \
  -v $PWD:/usr/src \
  -e LOG_LEVEL=info \
  -e GITHUB_COM_TOKEN="${GITHUB_TOKEN}" \
  renovate/renovate --platform github --autodiscover
