package org.balanceeat.domain.user

import org.balanceeat.domain.config.TestFixture
import java.time.LocalDateTime
import java.util.*

class UserResultFixture(
    var id: Long = 1L,
    var uuid: String = UUID.randomUUID().toString(),
    var name: String = "테스트 사용자",
    var email: String? = "test@example.com",
    var gender: User.Gender = User.Gender.MALE,
    var age: Int = 30,
    var weight: Double = 70.0,
    var height: Double = 175.5,
    var goalType: User.GoalType = User.GoalType.MAINTAIN,
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
    var providerType: String? = "GOOGLE",
    var createdAt: LocalDateTime = LocalDateTime.now()
) : TestFixture<UserResult> {
    override fun create(): UserResult {
        return UserResult(
            id = id,
            uuid = uuid,
            name = name,
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
            providerType = providerType,
            createdAt = createdAt
        )
    }
}

fun userResultFixture(block: UserResultFixture.() -> Unit = {}): UserResult {
    return UserResultFixture().apply(block).create()
}
