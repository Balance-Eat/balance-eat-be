package org.balanceeat.admin.api.food

import org.balanceeat.apibase.ApplicationStatus.FOOD_NOT_FOUND
import org.balanceeat.apibase.exception.NotFoundException
import org.balanceeat.domain.food.FoodCommand
import org.balanceeat.domain.food.FoodDomainService
import org.balanceeat.domain.food.FoodDto
import org.balanceeat.domain.food.FoodRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminFoodService(
    private val foodDomainService: FoodDomainService,
    private val foodRepository: FoodRepository
) {
    @Transactional(readOnly = true)
    fun getDetails(id: Long): FoodDto {
        return foodRepository.findByIdOrNull(id)
            ?.let { FoodDto.from(it) }
            ?: throw NotFoundException(FOOD_NOT_FOUND)
    }

    @Transactional
    fun update(request: AdminFoodV1Request.Update, id: Long, adminId: Long): FoodDto {
        return foodDomainService.update(
            command = FoodCommand.Update(
                id = id,
                name = request.name,
                perCapitaIntake = request.perCapitaIntake,
                unit = request.unit,
                carbohydrates = request.carbohydrates,
                protein = request.protein,
                fat = request.fat,
                isAdminApproved = request.isAdminApproved
            )
        )
    }
}