package org.balanceeat.domain.food

import org.springframework.data.domain.Pageable


class FoodCommand {
    data class Create(
        val uuid: String,
        val name: String,
        val userId: Long,
        val servingSize: Double,
        val unit: String,
        val carbohydrates: Double = 0.0,
        val protein: Double = 0.0,
        val fat: Double = 0.0,
        val brand: String,
        val isAdminApproved: Boolean = false
    )
    
    data class Update(
        val id: Long,
        val name: String,
        val servingSize: Double,
        val unit: String,
        val carbohydrates: Double,
        val protein: Double,
        val fat: Double,
        val brand: String,
        val isAdminApproved: Boolean
    )
}