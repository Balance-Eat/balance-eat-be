package org.balanceeat.domain.user

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.common.exceptions.BadRequestException
import org.balanceeat.domain.common.exceptions.NotFoundException
import org.balanceeat.domain.config.NEW_ID
import org.balanceeat.domain.config.supports.IntegrationTestContext
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
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
            val createCommand = UserCommandFixture.Create(
                uuid = uuid,
                name = "테스트 사용자",
                email = "test@example.com"
            ).create()

            // when
            userDomainService.create(createCommand)

            // then
            val savedUser = userRepository.findByUuid(uuid)
            assertThat(savedUser).isNotNull
            assertThat(savedUser!!.name).isEqualTo(createCommand.name)
            assertThat(savedUser.uuid).isEqualTo(createCommand.uuid)
            assertThat(savedUser.email).isEqualTo(createCommand.email)
            assertThat(savedUser.gender).isEqualTo(createCommand.gender)
            assertThat(savedUser.age).isEqualTo(createCommand.age)
            assertThat(savedUser.height).isEqualTo(createCommand.height)
            assertThat(savedUser.weight).isEqualTo(createCommand.weight)
            assertThat(savedUser.activityLevel).isEqualTo(createCommand.activityLevel)
            assertThat(savedUser.smi).isEqualTo(createCommand.smi)
            assertThat(savedUser.fatPercentage).isEqualTo(createCommand.fatPercentage)
            assertThat(savedUser.targetWeight).isEqualTo(createCommand.targetWeight)
            assertThat(savedUser.targetCalorie).isEqualTo(createCommand.targetCalorie)
            assertThat(savedUser.targetSmi).isEqualTo(createCommand.targetSmi)
            assertThat(savedUser.targetFatPercentage).isEqualTo(createCommand.targetFatPercentage)
            assertThat(savedUser.providerId).isEqualTo(createCommand.providerId)
            assertThat(savedUser.providerType).isEqualTo(createCommand.providerType)

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
                .isInstanceOf(BadRequestException::class.java)
                .hasFieldOrPropertyWithValue("status", DomainStatus.USER_ALREADY_EXISTS)
        }
    }

    @Nested
    @DisplayName("조회 테스트")
    inner class FindByIdTest {
        
        @Test
        fun `사용자 정보를 조회할 수 있다`() {
            // given
            val user = userRepository.save(UserFixture().create())

            // when
            val foundUser = userDomainService.findById(user.id)

            // then
            assertThat(foundUser.id).isEqualTo(user.id)
            assertThat(foundUser.name).isEqualTo(user.name)
            assertThat(foundUser.uuid).isEqualTo(user.uuid)
            assertThat(foundUser.email).isEqualTo(user.email)
            assertThat(foundUser.gender).isEqualTo(user.gender)
            assertThat(foundUser.age).isEqualTo(user.age)
            assertThat(foundUser.weight).isEqualTo(user.weight)
            assertThat(foundUser.height).isEqualTo(user.height)
            assertThat(foundUser.activityLevel).isEqualTo(user.activityLevel)
            assertThat(foundUser.smi).isEqualTo(user.smi)
            assertThat(foundUser.fatPercentage).isEqualTo(user.fatPercentage)
            assertThat(foundUser.targetWeight).isEqualTo(user.targetWeight)
            assertThat(foundUser.targetCalorie).isEqualTo(user.targetCalorie)
            assertThat(foundUser.targetSmi).isEqualTo(user.targetSmi)
            assertThat(foundUser.targetFatPercentage).isEqualTo(user.targetFatPercentage)
            assertThat(foundUser.providerId).isEqualTo(user.providerId)
            assertThat(foundUser.providerType).isEqualTo(user.providerType)
        }
    }

    @Nested
    @DisplayName("수정 테스트")
    inner class UpdateTest {
        
        @Test
        fun `사용자 정보를 수정할 수 있다`() {
            // given
            val user = userRepository.save(UserFixture().create())
            val command = UserCommand.Update(
                name = "수정된 사용자",
                email = "updated@example.com",
                gender = User.Gender.FEMALE,
                age = 31,
                height = 170.0,
                weight = 72.0,
                activityLevel = User.ActivityLevel.ACTIVE,
                smi = 25.0,
                fatPercentage = 18.0,
                targetWeight = 65.0,
                targetCalorie = 2100,
                targetSmi = 24.0,
                targetFatPercentage = 15.0
            )

            // when
            userDomainService.update(user.id, command)

            // then
            val updatedUser = userDomainService.findById(user.id)
            assertThat(updatedUser.name).isEqualTo(command.name)
            assertThat(updatedUser.email).isEqualTo(command.email)
            assertThat(updatedUser.gender).isEqualTo(command.gender)
            assertThat(updatedUser.age).isEqualTo(command.age)
            assertThat(updatedUser.height).isEqualTo(command.height)
            assertThat(updatedUser.weight).isEqualTo(command.weight)
            assertThat(updatedUser.activityLevel).isEqualTo(command.activityLevel)
            assertThat(updatedUser.smi).isEqualTo(command.smi)
            assertThat(updatedUser.fatPercentage).isEqualTo(command.fatPercentage)
            assertThat(updatedUser.targetWeight).isEqualTo(command.targetWeight)
            assertThat(updatedUser.targetCalorie).isEqualTo(command.targetCalorie)
            assertThat(updatedUser.targetSmi).isEqualTo(command.targetSmi)
            assertThat(updatedUser.targetFatPercentage).isEqualTo(command.targetFatPercentage)
            // UUID는 수정되지 않아야 함
            assertThat(updatedUser.uuid).isEqualTo(user.uuid)
            assertThat(updatedUser.id).isEqualTo(user.id)
        }
    }
}