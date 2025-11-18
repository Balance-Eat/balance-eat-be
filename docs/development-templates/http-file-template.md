# HTTP 파일 작성 가이드

## 개요

Balance-Eat 프로젝트에서 REST API 테스트를 위한 HTTP 파일 작성 표준을 정의합니다.
개발시 Controller에 메서드 추가/수정/삭제 시, 그에 대응되는 내용을 반영하여야 합니다.

## 기본 원칙

### 1. 컨트롤러 변경 사항 확인
- 컨트롤러에 새로운 엔드포인트가 추가되거나 기존 엔드포인트가 수정/삭제될 경우, 해당 내용을 HTTP 파일에 반영합니다.
- 컨트롤러 메서드가 삭제된 경우, 해당 HTTP 요청도 삭제합니다.

### 2. 성공 케이스 중심 작성
- **모든 컨트롤러 엔드포인트에 대해 성공 요청이 최소 1개 이상 존재해야 합니다**
- 모든 케이스(실패, 예외, 엣지 케이스)를 작성하지 말고 **일반적인 성공 케이스 1개만 작성**합니다

### 3. 파일 명명 규칙
- `{domain}-api.http` 형식으로 작성합니다
- 예: `user-api.http`, `diet-api.http`, `notification-api.http`

### 4. 환경 변수 사용
- `http-client.env.json`에 정의된 환경 변수를 사용합니다
- 기본 변수:
  - `{{baseUrl}}` 또는 `{{host}}`: 서버 주소
  - `{{apiVersion}}`: API 버전 (예: v1)

## HTTP 파일 구조

### 기본 템플릿

```http
### {API 설명}
{HTTP_METHOD} {{baseUrl}}/{{apiVersion}}/{endpoint}
Content-Type: application/json
{필요한 경우 추가 헤더}

{
  "필드명": "값"
}

###
```

### 섹션 구분
- `###`를 사용하여 각 요청을 구분합니다
- 각 요청 위에 설명 주석을 추가합니다 (`### {설명}`)

## 작성 예시

### 1. POST 요청 (생성)

```http
### 알림 디바이스 등록
POST {{host}}/v1/notification-devices
Content-Type: application/json
X-USER-ID: 1

{
  "agentId": "test-agent-id-123",
  "osType": "AOS",
  "deviceName": "갤럭시 S24",
  "allowsNotification": true
}

###
```

### 2. GET 요청 (조회)

```http
### 특정 날짜의 식사 기록 조회
GET {{baseUrl}}/{{apiVersion}}/diets/daily?date=2024-01-15
Accept: application/json
X-USER-ID: 1

###
```

### 3. PUT 요청 (수정)

```http
### 음식 정보 수정
PUT {{baseUrl}}/v1/foods/1
Content-Type: application/json

{
  "name": "김치찌개 (수정)",
  "calories": 280,
  "protein": 18.0
}

###
```

### 4. DELETE 요청 (삭제)

```http
### 식단 삭제
DELETE {{baseUrl}}/{{apiVersion}}/diets/1
X-USER-ID: 1

###
```

## 작성 가이드

### 1. 헤더 작성
- `Content-Type`: 요청 본문이 있는 경우 명시 (일반적으로 `application/json`)
- `Accept`: 응답 형식이 중요한 경우 명시
- 커스텀 헤더: 프로젝트별 요구사항에 따라 추가 (예: `X-USER-ID`)

### 2. 요청 본문 작성
- JSON 형식으로 작성
- 실제 사용 가능한 값 사용 (한글 포함)
- 필수 필드만 포함하되, 이해를 돕기 위한 선택 필드 추가 가능

## 체크리스트

HTTP 파일 작성 시 다음 사항을 확인합니다:

- [ ] **모든 컨트롤러 엔드포인트에 대한 성공 요청 1개 이상 작성**
- [ ] 파일명이 `{domain}-api.http` 형식으로 작성되었는가?
- [ ] 환경 변수(`{{baseUrl}}`, `{{apiVersion}}`)를 올바르게 사용하였는가?
- [ ] 각 요청에 설명 주석이 추가되었는가?
- [ ] `###`로 요청이 적절히 구분되었는가?
- [ ] 필요한 헤더가 모두 포함되었는가?
- [ ] 요청 본문이 유효한 JSON 형식인가?
- [ ] 실제 사용 가능한 값으로 작성되었는가?

## 참고 파일

프로젝트 내 기존 HTTP 파일 참고:
- `http/user-api.http`
- `http/diet-api.http`
- `http/food-api.http`
- `http/notification-api.http`
