package org.balanceeat.domain.user

import org.balanceeat.domain.config.TestFixture
import java.util.*

class UserCommandFixture(
    var uuid: String = UUID.randomUUID().toString(),
    var name: String = "테스트 사용자",
    var gender: User.Gender = User.Gender.MALE,
    var age: Int = 30,
    var height: Double = 175.5,
    var weight: Double = 70.0,
    var email: String? = "test@example.com",
    var activityLevel: User.ActivityLevel? = User.ActivityLevel.MODERATE,
    var smi: Double? = 22.5,
    var fatPercentage: Double? = 15.5,
    var targetWeight: Double? = 68.0,
    var targetCalorie: Int? = 2200,
    var targetSmi: Double? = 23.0,
    var targetFatPercentage: Double? = 12.0,
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
            email = email,
            activityLevel = activityLevel,
            smi = smi,
            fatPercentage = fatPercentage,
            targetWeight = targetWeight,
            targetCalorie = targetCalorie,
            targetSmi = targetSmi,
            targetFatPercentage = targetFatPercentage,
            providerId = providerId,
            providerType = providerType
        )
    }
}