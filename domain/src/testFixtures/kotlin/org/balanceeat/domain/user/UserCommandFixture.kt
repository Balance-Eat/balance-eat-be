package org.balanceeat.domain.user

import org.balanceeat.common.TestFixture
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

    class Update(
        var id: Long = 1L,
        var name: String = "수정된 사용자",
        var gender: User.Gender = User.Gender.FEMALE,
        var age: Int = 31,
        var height: Double = 170.0,
        var weight: Double = 72.0,
        var goalType: User.GoalType = User.GoalType.DIET,
        var email: String? = "updated@example.com",
        var activityLevel: User.ActivityLevel? = User.ActivityLevel.ACTIVE,
        var smi: Double? = 25.0,
        var fatPercentage: Double? = 18.0,
        var targetWeight: Double? = 65.0,
        var targetCalorie: Int? = 2100,
        var targetSmi: Double? = 24.0,
        var targetFatPercentage: Double? = 15.0,
        var targetCarbohydrates: Double? = 250.0,
        var targetProtein: Double? = 100.0,
        var targetFat: Double? = 60.0
    ) : TestFixture<UserCommand.Update> {
        override fun create(): UserCommand.Update {
            return UserCommand.Update(
                id = id,
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
                targetFat = targetFat
            )
        }
    }
}

fun userCreateCommandFixture(block: UserCommandFixture.Create.() -> Unit = {}): UserCommand.Create {
    return UserCommandFixture.Create().apply(block).create()
}

fun userUpdateCommandFixture(block: UserCommandFixture.Update.() -> Unit = {}): UserCommand.Update {
    return UserCommandFixture.Update().apply(block).create()
}