package org.balanceeat.domain.user

import org.balanceeat.domain.config.TestFixture
import java.util.*

class UserCommandFixture {
    class Create(
        var uuid: String = UUID.randomUUID().toString(),
        var name: String = "테스트 사용자",
        var gender: User.Gender = User.Gender.MALE,
        var age: Int = 30,
        var height: Double = 175.5,
        var weight: Double = 70.0,
        var goalType: User.GoalType = User.GoalType.MAINTAIN,
        var email: String? = "test@example.com",
        var activityLevel: User.ActivityLevel? = User.ActivityLevel.MODERATE,
        var smi: Double? = 22.5,
        var fatPercentage: Double? = 15.5,
        var targetWeight: Double? = 68.0,
        var targetCalorie: Int? = 2200,
        var targetSmi: Double? = 23.0,
        var targetFatPercentage: Double? = 12.0,
        var targetCarbohydrates: Double? = 300.0,
        var targetProtein: Double? = 120.0,
        var targetFat: Double? = 80.0,
        var providerId: String? = "google123",
        var providerType: String? = "GOOGLE"
    ) : TestFixture<UserCommand.Create> {
        override fun create(): UserCommand.Create {
            return UserCommand.Create(
                uuid = uuid,
                name = name,
                gender = gender,
                age = age,
                height = height,
                weight = weight,
                goalType = goalType,
                email = email,
                activityLevel = activityLevel,
                smi = smi,
                fatPercentage = fatPercentage,
                targetWeight = targetWeight,
                targetCalorie = targetCalorie,
                targetSmi = targetSmi,
                targetFatPercentage = targetFatPercentage,
                targetCarbohydrates = targetCarbohydrates,
                targetProtein = targetProtein,
                targetFat = targetFat,
                providerId = providerId,
                providerType = providerType
            )
        }
    }

    class UpdateByAdmin(
        var id: Long = 1L,
        var adminId: Long = 1L,
        var name: String? = "어드민 수정 사용자",
        var email: String? = "admin-updated@example.com",
        var gender: User.Gender? = User.Gender.FEMALE,
        var age: Int? = 25,
        var height: Double? = 165.0,
        var weight: Double? = 55.0,
        var goalType: User.GoalType? = User.GoalType.DIET,
        var activityLevel: User.ActivityLevel? = User.ActivityLevel.ACTIVE,
        var smi: Double? = 20.0,
        var fatPercentage: Double? = 18.0,
        var targetWeight: Double? = 52.0,
        var targetCalorie: Int? = 1800,
        var targetSmi: Double? = 21.0,
        var targetFatPercentage: Double? = 16.0,
        var targetCarbohydrates: Double? = 250.0,
        var targetProtein: Double? = 100.0,
        var targetFat: Double? = 60.0,
        var providerId: String? = "kakao456",
        var providerType: String? = "KAKAO"
    ) : TestFixture<UserCommand.UpdateByAdmin> {
        override fun create(): UserCommand.UpdateByAdmin {
            return UserCommand.UpdateByAdmin(
                id = id,
                adminId = adminId,
                name = name,
                email = email,
                gender = gender,
                age = age,
                height = height,
                weight = weight,
                goalType = goalType,
                activityLevel = activityLevel,
                smi = smi,
                fatPercentage = fatPercentage,
                targetWeight = targetWeight,
                targetCalorie = targetCalorie,
                targetSmi = targetSmi,
                targetFatPercentage = targetFatPercentage,
                targetCarbohydrates = targetCarbohydrates,
                targetProtein = targetProtein,
                targetFat = targetFat,
                providerId = providerId,
                providerType = providerType
            )
        }
    }
}