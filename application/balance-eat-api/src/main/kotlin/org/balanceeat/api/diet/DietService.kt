package org.balanceeat.api.diet

import org.balanceeat.apibase.ApplicationStatus
import org.balanceeat.apibase.exception.BadRequestException
import org.balanceeat.apibase.exception.NotFoundException
import org.balanceeat.domain.diet.DietCommand
import org.balanceeat.domain.diet.DietReader
import org.balanceeat.domain.diet.DietResult
import org.balanceeat.domain.diet.DietWriter
import org.balanceeat.domain.food.FoodReader
import org.balanceeat.domain.food.FoodResult
import org.balanceeat.domain.user.UserReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.YearMonth

@Service
class DietService(
    private val dietWriter: DietWriter,
    private val dietReader: DietReader,
    private val userReader: UserReader
) {
    @Transactional
    fun create(request: DietV1Request.Create, userId: Long): DietV1Response.Details {
        userReader.validateExistsUser(userId)

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
            dietWriter.create(command)
        )
    }

    @Transactional
    fun update(request: DietV1Request.Update, dietId: Long, userId: Long): DietV1Response.Details {
        userReader.validateExistsUser(userId)

        val diet = dietReader.findById(dietId)
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
            dietWriter.update(command)
        )
    }

    @Transactional
    fun delete(id: Long, userId: Long) {
        userReader.validateExistsUser(userId)

        val diet = dietReader.findById(id)

        if (diet == null) return

        if (diet.userId != userId) {
            throw BadRequestException(ApplicationStatus.CANNOT_MODIFY_DIET)
        }

        dietWriter.delete(id)
    }

    @Transactional
    fun deleteDietFood(dietId: Long, dietFoodId: Long, userId: Long) {
        userReader.validateExistsUser(userId)

        val diet = dietReader.findById(dietId)
            ?: throw NotFoundException(ApplicationStatus.DIET_NOT_FOUND)

        if (diet.userId != userId) {
            throw BadRequestException(ApplicationStatus.CANNOT_MODIFY_DIET)
        }

        val command = DietCommand.DeleteDietFood(
            dietId = dietId,
            dietFoodId = dietFoodId
        )

        dietWriter.deleteDietFood(command)
    }

    @Transactional
    fun updateDietFood(
        dietId: Long,
        dietFoodId: Long,
        request: DietV1Request.UpdateDietFood,
        userId: Long
    ): DietV1Response.Details {
        userReader.validateExistsUser(userId)

        val diet = dietReader.findById(dietId)
            ?: throw NotFoundException(ApplicationStatus.DIET_NOT_FOUND)

        if (diet.userId != userId) {
            throw BadRequestException(ApplicationStatus.CANNOT_MODIFY_DIET)
        }

        val command = DietCommand.UpdateDietFood(
            dietId = dietId,
            dietFoodId = dietFoodId,
            intake = request.intake
        )

        val result = dietWriter.updateDietFood(command)
        return DietV1Response.Details.from(result)
    }

    @Transactional(readOnly = true)
    fun getDailyDiets(userId: Long, date: LocalDate): List<DietV1Response.Summary> {
        return dietReader.findDailyDietSummaries(userId, date)
            .map { DietV1Response.Summary.from(it) }
    }

    @Transactional(readOnly = true)
    fun getMonthlyDiets(userId: Long, yearMonth: YearMonth): List<DietV1Response.Summary> {
        return dietReader.findMonthlyDietSummaries(userId, yearMonth)
            .map { DietV1Response.Summary.from(it) }
    }
}