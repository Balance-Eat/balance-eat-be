.PHONY: help init test docker build coverage lint lint-fix

GRADLEW = ./gradlew

help: ## 사용 가능한 명령을 보여줍니다.
	@echo "Available targets:" && \
	egrep -h '^[a-zA-Z_-]+:.*?##' $(MAKEFILE_LIST) | \
	awk 'BEGIN {FS = ":.*?## "}; {printf "  %-15s %s\n", $$1, $$2}'

init: ## 프로젝트 초기 세팅 (git hooks 등).
	@echo "⚙️ Setting up git hooks..."
	@git config core.hooksPath .githooks
	@echo "✅ Project initialized."

clean: ## Gradle 빌드 파일을 정리합니다.
	@echo "🧹 Cleaning Gradle build files..."
	@$(GRADLEW) clean

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

coverage: test ## 테스트 커버리지를 확인합니다 (80% 이상).
	@echo "📊 Checking test coverage..."
	@$(GRADLEW) jacocoTestReport jacocoTestCoverageVerification

lint: ## 코드 스타일을 검사합니다.
	@echo "🔍 Running ktlint..."
	@$(GRADLEW) ktlintCheck

lint-fix: ## 코드 스타일을 자동 수정합니다.
	@echo "🔧 Fixing code style..."
	@$(GRADLEW) ktlintFormat

