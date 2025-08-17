package org.balanceeat.domain.user

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.balanceeat.domain.common.exceptions.NotFoundException
import org.balanceeat.domain.config.supports.IntegrationTestContext
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.util.*

class UserDomainServiceTest : IntegrationTestContext() {

    @Autowired
    private lateinit var userDomainService: UserDomainService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `사용자 정보를 조회할 수 있다`() {
        // given
        val uuid = UUID.randomUUID().toString()
        val createCommand = UserCommand.Create(
            uuid = uuid,
            name = "테스트 사용자",
            gender = User.Gender.MALE,
            age = 30,
            height = BigDecimal("175.5"),
            weight = BigDecimal("70.0"),
            email = "test@example.com"
        )
        userDomainService.create(createCommand)
        val savedUser = userRepository.findByUuid(uuid)!!

        // when
        val foundUser = userDomainService.findById(savedUser.id)

        // then
        assertThat(foundUser.id).isEqualTo(savedUser.id)
        assertThat(foundUser.name).isEqualTo("테스트 사용자")
        assertThat(foundUser.uuid).isEqualTo(uuid)
        assertThat(foundUser.email).isEqualTo("test@example.com")
    }

    @Test
    fun `존재하지 않는 사용자 조회시 예외가 발생한다`() {
        // when & then
        assertThatThrownBy {
            userDomainService.findById(999L)
        }.isInstanceOf(NotFoundException::class.java)
            .hasMessageContaining("사용자를 찾을 수 없습니다")
    }

    @Test
    fun `사용자 정보를 수정할 수 있다`() {
        // given
        val uuid = UUID.randomUUID().toString()
        val createCommand = UserCommand.Create(
            uuid = uuid,
            name = "테스트 사용자",
            gender = User.Gender.MALE,
            age = 30,
            height = BigDecimal("175.5"),
            weight = BigDecimal("70.0"),
            email = "test@example.com"
        )
        userDomainService.create(createCommand)
        val savedUser = userRepository.findByUuid(uuid)!!

        val updateCommand = UserCommand.Update(
            name = "수정된 사용자",
            age = 31,
            weight = BigDecimal("72.0")
        )

        // when
        userDomainService.update(savedUser.id, updateCommand)

        // then
        val updatedUser = userDomainService.findById(savedUser.id)
        assertThat(updatedUser.name).isEqualTo("수정된 사용자")
        assertThat(updatedUser.age).isEqualTo(31)
        assertThat(updatedUser.weight).isEqualByComparingTo(BigDecimal("72.0"))
        assertThat(updatedUser.height).isEqualByComparingTo(BigDecimal("175.5")) // 수정되지 않은 값
        assertThat(updatedUser.email).isEqualTo("test@example.com") // 수정되지 않은 값
    }
}