GRADLEW = ./gradlew

test: ## 모든 유닛 테스트를 실행합니다.
	@echo "🧪 Running tests..."
	@$(GRADLEW) test