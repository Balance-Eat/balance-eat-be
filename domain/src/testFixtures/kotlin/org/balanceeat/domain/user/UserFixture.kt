package org.balanceeat.domain.user

import org.balanceeat.domain.config.TestFixture
import org.balanceeat.domain.config.NEW_ID
import java.util.UUID

class UserFixture(
    var id: Long = NEW_ID,
    var name: String = "테스트유저",
    var uuid: String = UUID.randomUUID().toString(),
    var email: String? = "test@example.com",
    var gender: User.Gender = User.Gender.MALE,
    var age: Int = 25,
    var weight: Double = 70.0,
    var height: Double = 170.0,
    var goalType: User.GoalType = User.GoalType.MAINTAIN,
    var activityLevel: User.ActivityLevel? = User.ActivityLevel.MODERATE,
    var smi: Double? = null,
    var fatPercentage: Double? = null,
    var targetWeight: Double? = null,
    var targetCalorie: Int? = null,
    var targetSmi: Double? = null,
    var targetFatPercentage: Double? = null,
    var targetCarbohydrates: Double? = null,
    var targetProtein: Double? = null,
    var targetFat: Double? = null,
    var providerId: String? = null,
    var providerType: String? = null
) : TestFixture<User> {
    override fun create(): User {
        return User(
            id = id,
            name = name,
            uuid = uuid,
            email = email,
            gender = gender,
            age = age,
            weight = weight,
            height = height,
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