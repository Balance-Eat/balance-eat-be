#!/bin/bash
# ==============================================
# Balance-Eat Simple Docker Build Script
# ==============================================

set -euo pipefail

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

log_info() { echo -e "${YELLOW}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# Simple build without advanced caching
build_simple() {
    local target=${1:-development}
    local image_name="balance-eat-api:latest"
    
    log_info "Building $image_name (target: $target)..."
    
    if docker build \
        --file Dockerfile \
        --tag "$image_name" \
        . ; then
        log_success "Successfully built: $image_name"
        
        # Show image info
        log_info "Image size: $(docker images --format "{{.Size}}" "$image_name" | head -1)"
        return 0
    else
        log_error "Build failed!"
        return 1
    fi
}

# Main
case "${1:-development}" in
    "development"|"dev")
        build_simple "development"
        ;;
    "production"|"prod")
        build_simple "production"
        ;;
    "testing"|"test")
        build_simple "testing"
        ;;
    *)
        echo "Usage: $0 [development|production|testing]"
        echo "Example: $0 development"
        exit 1
        ;;
esac