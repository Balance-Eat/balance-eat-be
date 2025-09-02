#!/bin/bash

# Exit on any error
set -e

echo "🔐 Starting ECR login..."
aws ecr --profile balance-eat get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 831926604059.dkr.ecr.ap-northeast-2.amazonaws.com
if [ $? -ne 0 ]; then
    echo "❌ ECR login failed"
    exit 1
fi
echo "✅ ECR login successful"

echo "🏗️ Building Docker image..."
docker build --no-cache -t balance-eat .
if [ $? -ne 0 ]; then
    echo "❌ Docker build failed"
    exit 1
fi
echo "✅ Docker build successful"

echo "🏷️ Tagging Docker image..."
docker tag balance-eat:latest 831926604059.dkr.ecr.ap-northeast-2.amazonaws.com/balance-eat:latest
if [ $? -ne 0 ]; then
    echo "❌ Docker tag failed"
    exit 1
fi
echo "✅ Docker tag successful"

echo "📤 Pushing Docker image to ECR..."
docker push 831926604059.dkr.ecr.ap-northeast-2.amazonaws.com/balance-eat:latest
if [ $? -ne 0 ]; then
    echo "❌ Docker push failed"
    exit 1
fi
echo "✅ Docker push successful"

echo "🎉 All operations completed successfully!"