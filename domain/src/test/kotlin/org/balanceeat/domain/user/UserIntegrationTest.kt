package org.balanceeat.domain.user

import org.assertj.core.api.Assertions.assertThat
import org.balanceeat.domain.config.supports.IntegrationTestContext
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.util.*

class UserIntegrationTest : IntegrationTestContext() {

    @Autowired
    private lateinit var userDomainService: UserDomainService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `사용자를 생성할 수 있다`() {
        // given
        val uuid = UUID.randomUUID().toString()
        val createCommand = UserCommand.Create(
            uuid = uuid,
            name = "테스트 사용자",
            gender = User.Gender.MALE,
            age = 30,
            height = BigDecimal("175.5"),
            weight = BigDecimal("70.0"),
            email = "test@example.com",
            activityLevel = User.ActivityLevel.MODERATE,
            smi = BigDecimal("22.5"),
            fatPercentage = BigDecimal("15.5"),
            targetWeight = BigDecimal("68.0"),
            targetCalorie = 2200,
            targetSmi = BigDecimal("23.0"),
            targetFatPercentage = BigDecimal("12.0"),
            providerId = "google123",
            providerType = "GOOGLE"
        )

        // when
        userDomainService.create(createCommand)

        // then
        val savedUser = userRepository.findByUuid(uuid)
        assertThat(savedUser).isNotNull
        assertThat(savedUser!!.name).isEqualTo("테스트 사용자")
        assertThat(savedUser.uuid).isEqualTo(uuid)
        assertThat(savedUser.email).isEqualTo("test@example.com")
        assertThat(savedUser.gender).isEqualTo(User.Gender.MALE)
        assertThat(savedUser.age).isEqualTo(30)
        assertThat(savedUser.height).isEqualByComparingTo(BigDecimal("175.5"))
        assertThat(savedUser.weight).isEqualByComparingTo(BigDecimal("70.0"))
        assertThat(savedUser.activityLevel).isEqualTo(User.ActivityLevel.MODERATE)
        assertThat(savedUser.smi).isEqualByComparingTo(BigDecimal("22.5"))
        assertThat(savedUser.fatPercentage).isEqualByComparingTo(BigDecimal("15.5"))
        assertThat(savedUser.targetWeight).isEqualByComparingTo(BigDecimal("68.0"))
        assertThat(savedUser.targetCalorie).isEqualTo(2200)
        assertThat(savedUser.targetSmi).isEqualByComparingTo(BigDecimal("23.0"))
        assertThat(savedUser.targetFatPercentage).isEqualByComparingTo(BigDecimal("12.0"))
        assertThat(savedUser.providerId).isEqualTo("google123")
        assertThat(savedUser.providerType).isEqualTo("GOOGLE")
        assertThat(savedUser.id).isGreaterThan(0L)
    }
}