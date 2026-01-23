---
name: feature-dev
description: 기능 개발 전체 워크플로우. 설계→구현→테스트→커버리지→리뷰→리팩토링→커밋 7단계.
disable-model-invocation: true
---
# Feature Development Workflow

## 단계 추적 프로토콜
이 워크플로우 시작 시:
1. TaskCreate로 7단계를 태스크로 생성
2. 각 단계 시작 시 해당 태스크를 `in_progress`로 변경 + "현재 **N단계: OO** 진행 중" announce
3. 단계 완료 시 `completed`로 변경 + 다음 단계 자동 안내
4. 모든 단계 완료 시 최종 체크리스트 확인

## 7단계 워크플로우

### 1단계: 설계
- 요구사항 분석, 영향 범위 파악
- 기존 코드 탐색 (관련 도메인 Reader/Writer/Service 확인)
- 변경 계획 수립

### 2단계: 구현
- domain 모듈: Entity, Result, Command, Query, Repository, Reader, Writer
- application 모듈: Service, V1Controller, V1Payload
- 새 도메인이면 `/new-domain` Skill 또는 `domain-builder` Agent 활용

### 3단계: 테스트 작성
- `test-writer` Agent 활용 가능
- TestFixture 생성 (Entity, Command, Payload)
- 통합 테스트: `IntegrationTestContext` 상속
- 컨트롤러 테스트: `ControllerTestContext` 상속
- 한국어 백틱 메서드명

### 4단계: 커버리지 확인
- `make coverage` 실행 (Hook이 테스트 후 자동 실행)
- Jacoco 80%+ 달성 확인
- 미달 시 테스트 추가

### 5단계: 코드리뷰
- `/code-review` 호출 (OMC agent)
- 리뷰 결과 확인 및 피드백 정리

### 6단계: 리팩토링
- `/simplify` 호출 (OMC skill)
- `make lint` 실행
- 코드리뷰 피드백 반영

### 7단계: 커밋
- `git-master` (OMC 자동 감지)
- 커밋 메시지 컨벤션: `[feat]`, `[fix]`, `[refactor]`, `[docs]`, `[chore]`, `[build]`

## 자동 전환 규칙
- 3단계(테스트) 완료 → Hook이 `make coverage` 자동 실행
- 4단계(커버리지) 80%+ 확인 → `/code-review` 안내
- 6단계(리팩토링) 완료 → `make lint` 자동 실행

## 회귀 루프 규칙
- 5단계(코드리뷰) 또는 6단계(리팩토링)에서 설계 변경 필요 → **1단계로 회귀**
- 구현 수정만 필요 → **2단계로 회귀**
- 회귀 시 해당 단계부터 태스크 상태를 `in_progress`로 재설정
- announce: "**회귀: N단계로 돌아갑니다** (사유: OO)"
