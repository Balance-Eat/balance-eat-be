package org.balanceeat.domain.user

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.common.exception.BadCommandException
import org.balanceeat.domain.common.exception.EntityNotFoundException
import org.balanceeat.domain.config.supports.IntegrationTestContext
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import java.util.*

class UserDomainServiceTest : IntegrationTestContext() {

    @Autowired
    private lateinit var userDomainService: UserDomainService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Nested
    @DisplayName("생성 테스트")
    inner class CreateTest {
        
        @Test
        fun `사용자를 생성할 수 있다`() {
            // given
            val uuid = UUID.randomUUID().toString()
            val createCommand = UserCommandFixture.Create().create()

            // when
            val result = userDomainService.create(createCommand)

            // then
            val savedUser = userRepository.findByUuid(uuid)
            assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(savedUser)

        }

        @Test
        fun `중복된 UUID로 사용자 생성시 예외가 발생한다`() {
            // given
            val uuid = UUID.randomUUID().toString()
            val alreadySavedUser = userRepository.save(UserFixture(uuid=uuid).create())

            val duplicateCommand = UserCommandFixture.Create(
                uuid = uuid,
                name = "중복 사용자",
                email = "duplicate@example.com"
            ).create()

            // when & then
            assertThatThrownBy { userDomainService.create(duplicateCommand) }
                .isInstanceOf(BadCommandException::class.java)
                .hasFieldOrPropertyWithValue("status", DomainStatus.USER_ALREADY_EXISTS)
        }
    }

    @Nested
    @DisplayName("사용자 존재 검증 테스트")
    inner class ValidateExistsUserTest {
        @Test
        fun `존재하는 사용자 ID로 검증시 예외가 발생하지 않는다`() {
            // given
            val user = userRepository.save(UserFixture().create())

            // when & then
            userDomainService.validateExistsUser(user.id)
        }
        
        @Test
        fun `존재하지 않는 사용자 ID로 검증시 예외가 발생한다`() {
            // given
            val nonExistentUserId = 999L

            // when & then
            assertThatThrownBy { userDomainService.validateExistsUser(nonExistentUserId) }
                .isInstanceOf(EntityNotFoundException::class.java)
                .hasFieldOrPropertyWithValue("status", DomainStatus.USER_NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("수정 테스트")
    inner class UpdateTest {
        
        @Test
        fun `사용자 정보를 수정할 수 있다`() {
            // given
            val user = userRepository.save(UserFixture().create())
            val command = UserCommandFixture.Update(
                id = user.id
            ).create()

            // when
            val result = userDomainService.update(command)

            // then
            val savedUser = userRepository.findByIdOrNull(user.id)

            assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(savedUser)
        }
    }
}