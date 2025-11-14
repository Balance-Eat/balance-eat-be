package org.balanceeat.api.food

import org.balanceeat.apibase.ApplicationStatus.CANNOT_MODIFY_FOOD
import org.balanceeat.apibase.ApplicationStatus.FOOD_NOT_FOUND
import org.balanceeat.apibase.exception.BadRequestException
import org.balanceeat.apibase.exception.NotFoundException
import org.balanceeat.apibase.response.PageResponse
import org.balanceeat.domain.curation.CurationFoodReader
import org.balanceeat.domain.food.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FoodService(
    private val foodWriter: FoodWriter,
    private val foodReader: FoodReader,
    private val curationFoodReader: CurationFoodReader
) {
    @Transactional(readOnly = true)
    fun getDetails(id: Long): FoodV1Response.Details {
        return foodReader.findById(id)
            ?.let { FoodV1Response.Details.from(it) }
            ?: throw NotFoundException(FOOD_NOT_FOUND)
    }

    @Transactional
    fun create(request: FoodV1Request.Create, creatorId: Long): FoodV1Response.Details {
        val result = foodWriter.create(
            command = FoodCommand.Create(
                uuid = request.uuid,
                userId = creatorId,
                name = request.name,
                servingSize = request.servingSize,
                unit = request.unit,
                carbohydrates = request.carbohydrates,
                protein = request.protein,
                fat = request.fat,
                brand = request.brand,
                isAdminApproved = false
            )
        )

        return FoodV1Response.Details.from(result)
    }

    @Transactional
    fun update(request: FoodV1Request.Update, id: Long, modifierId: Long): FoodV1Response.Details {
        val food = foodReader.findById(id)
            ?: throw NotFoundException(FOOD_NOT_FOUND)

        if (food.userId != modifierId) {
            throw BadRequestException(CANNOT_MODIFY_FOOD)
        }

        val result = foodWriter.update(
            command = FoodCommand.Update(
                id = id,
                name = request.name,
                servingSize = request.servingSize,
                unit = request.unit,
                carbohydrates = request.carbohydrates,
                protein = request.protein,
                fat = request.fat,
                brand = request.brand,
                isAdminApproved = false
            )
        )

        return FoodV1Response.Details.from(result)
    }

    @Transactional(readOnly = true)
    fun getRecommendations(limit: Int = 10): List<FoodV1Response.Details> {
        val pageable = PageRequest.of(0, limit)
        val curationFoodMap =  curationFoodReader.findRecommendedFoods(pageable)
            .associateBy { it.foodId }

        return foodReader.findAllByIds(curationFoodMap.keys)
            .sortedByDescending { curationFoodMap[it.id]?.weight }
            .map { FoodV1Response.Details.from(it) }
    }

    @Transactional(readOnly = true)
    fun search(request: FoodV1Request.Search, pageable: Pageable): PageResponse<FoodSearchResult>{
        val result =  foodReader.search(
            FoodQuery.Search(
                foodName = request.foodName,
                userId = request.userId,
                pageable = pageable
            )
        )

        return PageResponse.from(result)
    }
}