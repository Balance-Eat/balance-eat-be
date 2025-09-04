package org.balanceeat.admin.api.user

import org.balanceeat.common.TestFixture
import org.balanceeat.domain.user.User

class AdminUserV1RequestFixture {
    class Update(
        var name: String? = "어드민 수정 사용자",
        var email: String? = "admin-updated@example.com",
        var gender: User.Gender? = User.Gender.FEMALE,
        var age: Int? = 25,
        var height: Double? = 165.0,
        var weight: Double? = 55.0,
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
    ) : TestFixture<AdminUserV1Request.Update> {
        override fun create(): AdminUserV1Request.Update {
            return AdminUserV1Request.Update(
                name = name,
                email = email,
                gender = gender,
                age = age,
                height = height,
                weight = weight,
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