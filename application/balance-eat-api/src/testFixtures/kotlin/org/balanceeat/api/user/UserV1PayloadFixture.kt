package org.balanceeat.api.user

import org.balanceeat.common.TestFixture
import org.balanceeat.domain.config.NEW_ID
import org.balanceeat.domain.user.User
import java.util.*

class UserV1RequestFixture {
    class Create(
        var uuid: String = UUID.randomUUID().toString(),
        var name: String = "김철수",
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
    ) : TestFixture<UserV1Request.Create> {
        override fun create(): UserV1Request.Create {
            return UserV1Request.Create(
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
        var name: String = "김철수",
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
        var targetFat: Double? = 80.0
    ) : TestFixture<UserV1Request.Update> {
        override fun create(): UserV1Request.Update {
            return UserV1Request.Update(
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
                targetFat = targetFat
            )
        }
    }
}

class UserV1ResponseFixture {
    class Details(
        var id : Long = NEW_ID,
        var uuid : String = UUID.randomUUID().toString(),
        var name : String = "test-uuid-123",
        var gender : User.Gender = User.Gender.MALE,
        var age : Int = 30,
        var height: Double = 175.5,
        var weight: Double = 70.0,
        var goalType: User.GoalType = User.GoalType.MAINTAIN,
        var email: String? = "test-email-123",
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
    ): TestFixture<UserV1Response.Details> {
        override fun create(): UserV1Response.Details {
            return UserV1Response.Details(
                id = id,
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
}