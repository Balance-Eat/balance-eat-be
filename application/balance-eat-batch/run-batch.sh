#!/bin/bash

set -e

JOB_NAME=$1
JOB_PARAMS=${2:-""}

if [ -z "$JOB_NAME" ]; then
    echo "Usage: ./run-batch.sh <jobName> [jobParameters]"
    echo ""
    echo "Available jobs:"
    echo "  - sampleJob"
    echo "  - dataProcessJob"
    echo ""
    echo "Example:"
    echo "  ./run-batch.sh sampleJob message=Hello"
    echo "  ./run-batch.sh dataProcessJob targetDate=2024-12-04"
    exit 1
fi

echo "=== Balance-Eat Batch Execution ==="
echo "Job Name: $JOB_NAME"
echo "Job Parameters: $JOB_PARAMS"
echo ""

java -jar build/libs/*.jar \
    --spring.batch.job.name=$JOB_NAME \
    $JOB_PARAMS \
    --spring.profiles.active=local

echo ""
echo "=== Batch execution completed ==="
