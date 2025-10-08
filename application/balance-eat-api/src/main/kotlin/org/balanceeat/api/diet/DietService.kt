package org.balanceeat.api.diet

import org.balanceeat.apibase.ApplicationStatus
import org.balanceeat.apibase.exception.BadRequestException
import org.balanceeat.apibase.exception.NotFoundException
import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.common.exception.DomainException
import org.balanceeat.domain.diet.Diet
import org.balanceeat.domain.diet.DietCommand
import org.balanceeat.domain.diet.DietDomainService
import org.balanceeat.domain.diet.DietRepository
import org.balanceeat.domain.food.Food
import org.balanceeat.domain.food.FoodRepository
import org.balanceeat.domain.user.UserDomainService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.YearMonth

@Service
class DietService(
    private val dietDomainService: DietDomainService,
    private val userDomainService: UserDomainService,
    private val dietRepository: DietRepository,
    private val foodRepository: FoodRepository
) {
    @Transactional
    fun create(request: DietV1Request.Create, userId: Long): DietV1Response.Details {
        userDomainService.validateExistsUser(userId)

        val command = DietCommand.Create(
            userId = userId,
            mealType = request.mealType,
            consumedAt = request.consumedAt,
            dietFoods = request.dietFoods.map { dietFood ->
                DietCommand.Create.DietFood(
                    foodId = dietFood.foodId,
                    intake = dietFood.intake
                )
            }
        )
        
        return DietV1Response.Details.from(
            dietDomainService.create(command)
        )
    }

    @Transactional
    fun update(request: DietV1Request.Update, dietId: Long, userId: Long): DietV1Response.Details {
        userDomainService.validateExistsUser(userId)

        val diet = dietRepository.findByIdOrNull(dietId)
            ?: throw NotFoundException(ApplicationStatus.DIET_NOT_FOUND)

        if (diet.userId != userId) {
            throw BadRequestException(ApplicationStatus.CANNOT_MODIFY_FOOD)
        }

        val command = DietCommand.Update(
            id = dietId,
            mealType = request.mealType,
            consumedAt = request.consumedAt,
            dietFoods = request.dietFoods.map { dietFood ->
                DietCommand.Update.DietFood(
                    foodId = dietFood.foodId,
                    intake = dietFood.intake
                )
            }
        )

        return DietV1Response.Details.from(
            dietDomainService.update(command)
        )
    }

    @Transactional(readOnly = true)
    fun getDailyDiets(userId: Long, date: LocalDate): List<DietV1Response.Summary> {
        val diets = dietRepository.findDailyDiets(userId, date)
        val foodMap = getFoodMap(diets)

        return diets.map {
            DietV1Response.Summary.of(it, foodMap)
        }
    }

    @Transactional(readOnly = true)
    fun getMonthlyDiets(userId: Long, yearMonth: YearMonth): List<DietV1Response.Summary> {
        val diets = dietRepository.findMonthlyDiets(userId, yearMonth)
        val foodMap = getFoodMap(diets)

        return diets.map {
            DietV1Response.Summary.of(it, foodMap)
        }
    }

    private fun getFoodMap(diets: List<Diet>): Map<Long, Food> {
        return diets.flatMap { it.dietFoods }
            .map { it.foodId }
            .distinct()
            .let { foodRepository.findAllById(it) }
            .associateBy { it.id }
    }
}