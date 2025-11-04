package org.balanceeat.domain.diet

import org.balanceeat.domain.common.DomainService
import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.common.exception.DomainException
import org.balanceeat.domain.common.exception.EntityNotFoundException
import org.balanceeat.domain.food.FoodRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@DomainService
class DietDomainService(
    private val dietRepository: DietRepository,
    private val foodRepository: FoodRepository,
    private val eventPublisher: ApplicationEventPublisher
) {
    @Transactional
    fun create(command: DietCommand.Create): DietDto {
        checkDuplication(
            userId = command.userId,
            consumedAt = command.consumedAt,
            mealType = command.mealType
        )

        val foodIds = command.dietFoods.map { it.foodId }
        val foodMap = foodRepository.findAllById(foodIds).associateBy { it.id }

        val diet = Diet(
            userId = command.userId,
            mealType = command.mealType,
            consumedAt = command.consumedAt
        )

        command.dietFoods.forEach { foodInfo ->
            val food = foodMap[foodInfo.foodId]
                ?: throw EntityNotFoundException(DomainStatus.FOOD_NOT_FOUND)
            diet.addFood(food, foodInfo.intake)
        }
        
        val savedDiet = dietRepository.save(diet)

        eventPublisher.publishEvent(DietCreatedEvent(savedDiet.id))

        return DietDto.from(savedDiet, foodMap)
    }

    @Transactional
    fun update(command: DietCommand.Update): DietDto {
        val diet = dietRepository.findByIdOrNull(command.id)
            ?: throw EntityNotFoundException(DomainStatus.DIET_NOT_FOUND)

        checkDuplication(
            userId = diet.userId,
            consumedAt = command.consumedAt,
            mealType = command.mealType,
            excludeId = diet.id
        )

        val foodIds = command.dietFoods.map { it.foodId }
        val foodMap = foodRepository.findAllById(foodIds).associateBy { it.id }
        val newDietFoods = command.dietFoods.map {
            val food = foodMap[it.foodId]
                ?: throw EntityNotFoundException(DomainStatus.FOOD_NOT_FOUND)
            DietFood(food, it.intake)
        }

        diet.update(command.mealType, command.consumedAt)
        diet.updateFoods(newDietFoods)

        val savedDiet = dietRepository.save(diet)

        eventPublisher.publishEvent(DietUpdatedEvent(savedDiet.id))

        return DietDto.from(savedDiet, foodMap)
    }

    @Transactional
    fun delete(id: Long) {
        dietRepository.findById(id)
            .ifPresent {
                eventPublisher.publishEvent(DietDeletedEvent(it.id))
                dietRepository.delete(it)
            }
    }

    @Transactional
    fun deleteDietFood(command: DietCommand.DeleteDietFood) {
        val diet = dietRepository.findByIdOrNull(command.dietId)
            ?: throw EntityNotFoundException(DomainStatus.DIET_NOT_FOUND)

        diet.removeFood(command.dietFoodId)
        dietRepository.save(diet)
        eventPublisher.publishEvent(DietUpdatedEvent(diet.id))
    }

    @Transactional
    fun updateDietFood(command: DietCommand.UpdateDietFood): DietDto {
        val diet = dietRepository.findByIdOrNull(command.dietId)
            ?: throw EntityNotFoundException(DomainStatus.DIET_NOT_FOUND)

        diet.updateFood(command.dietFoodId, command.intake)
        val savedDiet = dietRepository.save(diet)
        val foodIds = savedDiet.dietFoods.map { it.foodId }
        val foodMap = foodRepository.findAllById(foodIds).associateBy { it.id }

        eventPublisher.publishEvent(DietUpdatedEvent(diet.id))

        return DietDto.from(savedDiet, foodMap)
    }

    private fun checkDuplication(
        userId: Long,
        consumedAt: LocalDateTime,
        mealType: Diet.MealType,
        excludeId: Long = 0L
    ) {
        val diets = dietRepository.findDailyDiets(userId, consumedAt.toLocalDate())
        val isDuplicate = diets.filter { it.id != excludeId }
            .map { it.mealType }
            .any { it == mealType }

        if (isDuplicate) {
            throw DomainException(DomainStatus.DIET_MEAL_TYPE_ALREADY_EXISTS)
        }
    }
}