package org.balanceeat.api.food

import org.balanceeat.apibase.ApplicationStatus.CANNOT_MODIFY_FOOD
import org.balanceeat.apibase.ApplicationStatus.FOOD_NOT_FOUND
import org.balanceeat.apibase.exception.BadRequestException
import org.balanceeat.apibase.exception.NotFoundException
import org.balanceeat.apibase.response.PageResponse
import org.balanceeat.domain.curation.CurationFoodRepository
import org.balanceeat.domain.food.FoodCommand
import org.balanceeat.domain.food.FoodDomainService
import org.balanceeat.domain.food.FoodQuery
import org.balanceeat.domain.food.FoodResult
import org.balanceeat.domain.food.FoodRepository
import org.balanceeat.domain.food.FoodSearchResult
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FoodService(
    private val foodDomainService: FoodDomainService,
    private val foodRepository: FoodRepository,
    private val curationFoodRepository: CurationFoodRepository
) {
    @Transactional(readOnly = true)
    fun getDetails(id: Long): FoodV1Response.Details {
        return foodRepository.findByIdOrNull(id)
            ?.let { FoodResult.from(it) }
            ?.let { FoodV1Response.Details.from(it) }
            ?: throw NotFoundException(FOOD_NOT_FOUND)
    }

    @Transactional
    fun create(request: FoodV1Request.Create, creatorId: Long): FoodV1Response.Details {
        val result = foodDomainService.create(
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
        val food = foodRepository.findByIdOrNull(id)
            ?: throw NotFoundException(FOOD_NOT_FOUND)

        if (food.userId != modifierId) {
            throw BadRequestException(CANNOT_MODIFY_FOOD)
        }

        val result = foodDomainService.update(
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
        val curationFoodMap =  curationFoodRepository.findRecommendedFoods(pageable)
            .associateBy { it.foodId }

        return foodRepository.findAllById(curationFoodMap.keys)
            .map { FoodResult.from(it) }
            .sortedByDescending { curationFoodMap[it.id]?.weight }
            .map { FoodV1Response.Details.from(it) }
    }

    @Transactional(readOnly = true)
    fun search(request: FoodV1Request.Search, pageable: Pageable): PageResponse<FoodSearchResult>{
        val result =  foodRepository.search(
            FoodQuery.Search(
                foodName = request.foodName,
                userId = request.userId,
                pageable = pageable
            )
        )

        return PageResponse.from(result)
    }
}