.PHONY: help test docker build

GRADLEW = ./gradlew

help: ## 사용 가능한 명령을 보여줍니다.
	@echo "Available targets:" && \
	egrep -h '^[a-zA-Z_-]+:.*?##' $(MAKEFILE_LIST) | \
	awk 'BEGIN {FS = ":.*?## "}; {printf "  %-15s %s\n", $$1, $$2}'

test: ## 모든 유닛 테스트를 실행합니다.
	@echo "🧪 Running tests..."
	@$(GRADLEW) test

openapi: ## OpenAPI 스펙을 생성합니다.
	@echo "📄 Generating OpenAPI specification..."
	@$(GRADLEW) :application:balance-eat-api:openapi3

docker: ## Docker 이미지를 빌드합니다.
	@./docker-build.sh

build: ## 테스트 후 Docker 이미지 빌드까지 수행합니다.
	@$(MAKE) test || (echo "❌ 테스트 실패. 빌드를 중단합니다." && exit 1)
	@$(MAKE) openapi || (echo "❌ OpenAPI 스펙 생성 실패. 빌드를 중단합니다." && exit 1)
	@$(MAKE) docker
	@echo "✅ Build completed successfully."

