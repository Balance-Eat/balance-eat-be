#!/bin/bash

aws ecr --profile balance-eat get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 831926604059.dkr.ecr.ap-northeast-2.amazonaws.com
docker build --no-cache -t balance-eat .
docker tag balance-eat:latest 831926604059.dkr.ecr.ap-northeast-2.amazonaws.com/balance-eat:latest
docker push 831926604059.dkr.ecr.ap-northeast-2.amazonaws.com/balance-eat:latest