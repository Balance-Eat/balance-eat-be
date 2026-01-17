---
name: api-docs
description: OpenAPI3 문서 생성 및 검증 워크플로우. OpenAPI/Swagger/RestDocs 관련 작업 시 자동 호출.
---
# OpenAPI Documentation Workflow

## 생성 절차
1. 컨트롤러 테스트 작성 (RestDocs snippet 포함)
2. `make openapi` 실행
3. 생성된 YAML 검증: `application/balance-eat-api/src/main/resources/static/api-docs/balanceeat/main.yaml`

## Tags enum 등록
- `Tags` enum에 새 도메인 태그 추가
- `tagName`: 표시명
- `descriptionWith("작업명")`: 태그 설명 생성

## RestDocs 헬퍼
```kotlin
"field" type TYPE means "설명"
"field" type STRING means "설명" isOptional true
"field" type STRING means "설명" withEnum Enum::class
"id" pathMeans "설명"
"param" queryMeans "설명"
fieldsWithBasic(...)
```

## 검증
- `make openapi` 성공 확인
- YAML 파일에 새 endpoint 포함 확인
- Tags 정상 등록 확인
