package org.balanceeat.admin.api.food

import org.balanceeat.apibase.ApplicationStatus.FOOD_NOT_FOUND
import org.balanceeat.apibase.exception.NotFoundException
import org.balanceeat.domain.food.FoodCommand
import org.balanceeat.domain.food.FoodWriter
import org.balanceeat.domain.food.FoodResult
import org.balanceeat.domain.food.FoodRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminFoodService(
    private val foodWriter: FoodWriter,
    private val foodRepository: FoodRepository
) {
    @Transactional(readOnly = true)
    fun getDetails(id: Long): AdminFoodV1Response.Details {
        return foodRepository.findByIdOrNull(id)
            ?.let { FoodResult.from(it) }
            ?.let { AdminFoodV1Response.Details.from(it) }
            ?: throw NotFoundException(FOOD_NOT_FOUND)
    }

    @Transactional
    fun update(request: AdminFoodV1Request.Update, id: Long, adminId: Long): AdminFoodV1Response.Details {
        val result =  foodWriter.update(
            command = FoodCommand.Update(
                id = id,
                name = request.name,
                servingSize = request.servingSize,
                unit = request.unit,
                carbohydrates = request.carbohydrates,
                protein = request.protein,
                fat = request.fat,
                brand = request.brand,
                isAdminApproved = request.isAdminApproved
            )
        )

        return AdminFoodV1Response.Details.from(result)
    }
}