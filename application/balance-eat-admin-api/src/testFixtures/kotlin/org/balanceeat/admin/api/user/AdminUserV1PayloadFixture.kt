package org.balanceeat.admin.api.user

import org.balanceeat.common.TestFixture
import org.balanceeat.domain.user.User
import java.util.*

class AdminUserV1RequestFixture {
    class Update(
        var name: String = "관리자 수정 사용자",
        var gender: User.Gender = User.Gender.FEMALE,
        var age: Int = 28,
        var height: Double = 165.0,
        var weight: Double = 55.0,
        var goalType: User.GoalType = User.GoalType.DIET,
        var email: String? = "admin@example.com",
        var activityLevel: User.ActivityLevel? = User.ActivityLevel.ACTIVE,
        var smi: Double? = 20.5,
        var fatPercentage: Double? = 18.0,
        var targetWeight: Double? = 50.0,
        var targetCalorie: Int? = 1800,
        var targetSmi: Double? = 21.0,
        var targetFatPercentage: Double? = 15.0,
        var targetCarbohydrates: Double? = 250.0,
        var targetProtein: Double? = 100.0,
        var targetFat: Double? = 60.0
    ) : TestFixture<AdminUserV1Request.Update> {
        override fun create(): AdminUserV1Request.Update {
            return AdminUserV1Request.Update(
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

class AdminUserV1ResponseFixture {
    class Details(
        var id: Long = 1L,
        var uuid: String = UUID.randomUUID().toString(),
        var name: String = "관리자 응답 사용자",
        var email: String? = "admin-response@example.com",
        var gender: User.Gender = User.Gender.MALE,
        var age: Int = 32,
        var height: Double = 175.0,
        var weight: Double = 70.0,
        var goalType: User.GoalType = User.GoalType.MAINTAIN,
        var activityLevel: User.ActivityLevel? = User.ActivityLevel.MODERATE,
        var smi: Double? = 22.5,
        var fatPercentage: Double? = 15.0,
        var targetWeight: Double? = 68.0,
        var targetCalorie: Int? = 2200,
        var targetSmi: Double? = 23.0,
        var targetFatPercentage: Double? = 12.0,
        var targetCarbohydrates: Double? = 300.0,
        var targetProtein: Double? = 120.0,
        var targetFat: Double? = 80.0,
        var providerId: String? = "google123",
        var providerType: String? = "GOOGLE"
    ) : TestFixture<AdminUserV1Response.Details> {
        override fun create(): AdminUserV1Response.Details {
            return AdminUserV1Response.Details(
                id = id,
                uuid = uuid,
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

fun adminUserV1RequestUpdate(block: AdminUserV1RequestFixture.Update.() -> Unit = {}): AdminUserV1Request.Update {
    return AdminUserV1RequestFixture.Update().apply(block).create()
}

fun adminUserV1ResponseDetails(block: AdminUserV1ResponseFixture.Details.() -> Unit = {}): AdminUserV1Response.Details {
    return AdminUserV1ResponseFixture.Details().apply(block).create()
}