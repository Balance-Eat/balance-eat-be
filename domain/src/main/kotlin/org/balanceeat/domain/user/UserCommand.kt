package org.balanceeat.domain.user

import java.math.BigDecimal

class UserCommand {

    data class Create(
        val uuid: String,
        val name: String,
        val gender: User.Gender,
        val age: Int,
        val height: BigDecimal,
        val weight: BigDecimal,
        val email: String? = null,
        val activityLevel: User.ActivityLevel? = null,
        val smi: BigDecimal? = null,
        val fatPercentage: BigDecimal? = null,
        val targetWeight: BigDecimal? = null,
        val targetCalorie: Int? = null,
        val targetSmi: BigDecimal? = null,
        val targetFatPercentage: BigDecimal? = null,
        val providerId: String? = null,
        val providerType: String? = null
    )
}
