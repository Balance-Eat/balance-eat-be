package org.balanceeat.domain.food


sealed class FoodCommand {
    
    data class Create(
        val uuid: String,
        val name: String,
        val userId: Long,
        val perCapitaIntake: Double,
        val unit: String,
        val carbohydrates: Double = 0.0,
        val protein: Double = 0.0,
        val fat: Double = 0.0,
        val isAdminApproved: Boolean = false
    )
    
    data class Update(
        val id: Long,
        val name: String,
        val perCapitaIntake: Double,
        val unit: String,
        val carbohydrates: Double,
        val protein: Double,
        val fat: Double,
        val isAdminApproved: Boolean
    )

    data class UpdateByAdmin(
        val id: Long,
        val adminId: Long,
        val name: String,
        val perCapitaIntake: Double,
        val unit: String,
        val carbohydrates: Double,
        val protein: Double,
        val fat: Double,
        val isAdminApproved: Boolean
    )
}