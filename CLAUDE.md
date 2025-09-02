# Balance-Eat Project Guidelines

This document contains project-specific guidelines and conventions for the Balance-Eat application.

## Documentation Index

### Project Guidelines
- **[Project Structure Guidelines](claude/project-structure.md)** - Project architecture, module structure, and development standards
- **[Entity Guidelines](claude/entity-guidelines.md)** - JPA entity design patterns and validation standards
- **[Feature Development Guide](claude/feature-development-guide.md)** - Complete development workflow and test specifications

### Testing Guidelines
- **[Test Fixture Guidelines](claude/fixture-guidelines.md)** - Standardized fixture patterns and best practices

## Quick Reference

### Project Architecture
See [claude/project-structure.md](claude/project-structure.md) for complete project structure documentation.

**Key Modules**:
- `application/balance-eat-api/` - REST API layer
- `domain/` - Core business logic and entities
- `supports/` - Common utilities (jackson, monitoring)

### File Naming Conventions
- **Entities**: `{Domain}.kt` (User.kt, Food.kt)
- **Commands**: `{Domain}Command.kt` (UserCommand.kt)
- **Services**: `{Domain}DomainService.kt` (UserDomainService.kt)
- **Controllers**: `{Domain}V1Controller.kt` (UserV1Controller.kt)
- **Tests**: `{Domain}DomainServiceTest.kt`
- **Fixtures**: `{Entity}Fixture.kt` (UserFixture.kt)

### Test Fixtures
See [claude/fixture-guidelines.md](claude/fixture-guidelines.md) for complete fixture documentation.

## Development Workflow

### Feature Development
새로운 기능 개발 요청 시, **[Feature Development Guide](claude/feature-development-guide.md)**를 기반으로 다음 순서로 진행합니다:

1. **Domain Layer**: Entity → Command → DTO → Repository → Domain Service
2. **Application Layer**: Application Service → API Payload → API Spec → Controller
3. **Testing**: Entity Fixture → Command Fixture → API Request Fixture → Tests
4. **Validation**: 개발 체크리스트 및 명명 규칙 준수 확인

### Development Standards

#### Code Quality
- Follow established fixture patterns consistently
- Use realistic Korean test data
- Maintain one responsibility per test file
- Include comprehensive test coverage

For detailed guidelines, see the linked documentation files above.