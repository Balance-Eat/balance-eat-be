package org.balanceeat.api.user

import org.assertj.core.api.Assertions.assertThat
import org.balanceeat.api.config.supports.IntegrationTestContext
import org.balanceeat.domain.user.User
import org.balanceeat.domain.user.UserFixture
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class UserServiceTest: IntegrationTestContext() {
    @Autowired
    private lateinit var userService: UserService

    @Nested
    inner class CreateTest {
        @Test
        fun `사용자 생성이 정상 동작한다`() {
            // given
            val request = UserV1RequestFixture.Create().create()

            // when
            val result = userService.create(request)

            // then
            assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(request)
        }

        @Test
        fun `필수 필드만으로 사용자 생성이 정상 동작한다`() {
            // given
            val request = UserV1RequestFixture.Create(
                email = null,
                activityLevel = null,
                smi = null,
                fatPercentage = null,
                targetWeight = null,
                targetCalorie = null,
                targetSmi = null,
                targetFatPercentage = null,
                targetCarbohydrates = null,
                targetProtein = null,
                targetFat = null,
                providerId = null,
                providerType = null
            ).create()

            // when
            val result = userService.create(request)

            // then
            assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(request)
        }
    }

    @Nested
    inner class UpdateTest {
        @Test
        fun `사용자 정보 수정이 정상 동작한다`() {
            // given
            val user = createEntity(UserFixture().create())
            val request = UserV1RequestFixture.Update().create()

            // when
            val result = userService.update(user.id, request)

            // then
            assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id", "uuid", "providerType", "providerId")
                .isEqualTo(request)
        }
    }

    @Nested
    inner class FindByUuidTest {
        @Test
        fun `UUID로 사용자 조회가 정상 동작한다`() {
            // given
            val user = createEntity(UserFixture(
                name = "테스트 사용자",
                email = "test@example.com",
                gender = User.Gender.MALE,
                age = 30,
                height = 175.5,
                weight = 70.0,
                goalType = User.GoalType.MAINTAIN
            ).create())

            // when
            val result = userService.findByUuid(user.uuid)

            // then
            assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(user)
        }
    }

}