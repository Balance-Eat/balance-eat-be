package org.balanceeat.domain.food

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

@QueryProjection
data class FoodSearchResult(
    val id: Long,
    val uuid: String,
    val name: String,
    val userId: Long,
    val perCapitaIntake: Double,
    val unit: String,
    val carbohydrates: Double,
    val protein: Double,
    val fat: Double,
    val brand: String,
    val isAdminApproved: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)