package org.balanceeat.domain.user


class UserCommand {

    data class Create(
        val uuid: String,
        val name: String,
        val gender: User.Gender,
        val age: Int,
        val height: Double,
        val weight: Double,
        val email: String? = null,
        val activityLevel: User.ActivityLevel? = null,
        val smi: Double? = null,
        val fatPercentage: Double? = null,
        val targetWeight: Double? = null,
        val targetCalorie: Int? = null,
        val targetSmi: Double? = null,
        val targetFatPercentage: Double? = null,
        val targetCarbohydrates: Double? = null,
        val targetProtein: Double? = null,
        val targetFat: Double? = null,
        val providerId: String? = null,
        val providerType: String? = null
    )

    data class Update(
        val name: String? = null,
        val email: String? = null,
        val gender: User.Gender? = null,
        val age: Int? = null,
        val height: Double? = null,
        val weight: Double? = null,
        val activityLevel: User.ActivityLevel? = null,
        val smi: Double? = null,
        val fatPercentage: Double? = null,
        val targetWeight: Double? = null,
        val targetCalorie: Int? = null,
        val targetSmi: Double? = null,
        val targetFatPercentage: Double? = null,
        val targetCarbohydrates: Double? = null,
        val targetProtein: Double? = null,
        val targetFat: Double? = null
    )
}
