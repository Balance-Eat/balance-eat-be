package org.balanceeat.domain.diet

import org.assertj.core.api.Assertions.assertThat
import org.balanceeat.domain.config.supports.IntegrationTestContext
import org.balanceeat.domain.food.FoodDomainService
import org.balanceeat.domain.food.FoodFixture
import org.balanceeat.domain.food.FoodRepository
import org.balanceeat.domain.user.UserFixture
import org.balanceeat.domain.user.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

class DietDomainServiceTest : IntegrationTestContext() {

    @Autowired
    private lateinit var dietDomainService: DietDomainService

    @Autowired
    private lateinit var dietRepository: DietRepository
    
    @Autowired
    private lateinit var userRepository: UserRepository
    
    @Autowired
    private lateinit var foodRepository: FoodRepository
    
    @Autowired
    private lateinit var foodDomainService: FoodDomainService

    @Test
    @Transactional
    fun `식사 기록을 생성할 수 있다`() {
        // given
        val user = userRepository.save(UserFixture().create())
        val food = foodRepository.save(FoodFixture().create())
        val command = DietCommandFixture(
            userId = user.id,
            foods = mutableListOf(
                DietCommand.AddFood(
                    foodId = food.id,
                    actualServingSize = 1.0,
                    servingUnit = "개"
                )
            )
        ).create()

        // when
        val savedDiet = dietDomainService.create(command)

        // then
        assertThat(savedDiet.id).isGreaterThan(0L)
        assertThat(savedDiet.userId).isEqualTo(user.id)
        assertThat(savedDiet.mealType).isEqualTo(Diet.MealType.BREAKFAST)
        assertThat(savedDiet.dietFoods).hasSize(1)
        assertThat(savedDiet.getTotalCalories()).isEqualTo(100.0)
    }

    @Test
    fun `사용자 ID와 날짜로 식사 기록을 조회할 수 있다`() {
        // given
        val user = userRepository.save(UserFixture(
            name = "테스트 사용자",
            email = "testuser@example.com"
        ).create())
        
        val food1 = foodRepository.save(FoodFixture(name = "아침 음식").create())
        val food2 = foodRepository.save(FoodFixture(name = "점심 음식").create())
        val mealDate = LocalDate.of(2024, 1, 15)
        
        // 아침 식사
        val breakfastCommand = DietCommandFixture(
            userId = user.id,
            mealType = Diet.MealType.BREAKFAST,
            mealDate = mealDate,
            consumedAt = mealDate.atTime(8, 0),
            foods = mutableListOf(
                DietCommand.AddFood(
                    foodId = food1.id,
                    actualServingSize = 1.0,
                    servingUnit = "개"
                )
            )
        ).create()
        dietDomainService.create(breakfastCommand)
        
        // 점심 식사
        val lunchCommand = DietCommandFixture(
            userId = user.id,
            mealType = Diet.MealType.LUNCH,
            mealDate = mealDate,
            consumedAt = mealDate.atTime(12, 0),
            foods = mutableListOf(
                DietCommand.AddFood(
                    foodId = food2.id,
                    actualServingSize = 1.0,
                    servingUnit = "개"
                )
            )
        ).create()
        dietDomainService.create(lunchCommand)

        // when
        val diets = dietDomainService.findByUserIdAndDate(user.id, mealDate)

        // then
        assertThat(diets).hasSize(2)
        assertThat(diets[0].mealType).isEqualTo(Diet.MealType.BREAKFAST)
        assertThat(diets[1].mealType).isEqualTo(Diet.MealType.LUNCH)
    }

    @Test
    @Transactional
    fun `사용자 UUID와 날짜로 식사 기록을 조회할 수 있다`() {
        // given
        val user = userRepository.save(UserFixture().create())
        val food = foodRepository.save(FoodFixture(name = "UUID 조회 테스트").create())
        val mealDate = LocalDate.of(2024, 2, 15) // 다른 날짜 사용
        
        val command = DietCommandFixture(
            userId = user.id,
            mealDate = mealDate,
            consumedAt = mealDate.atTime(8, 0),
            foods = mutableListOf(
                DietCommand.AddFood(
                    foodId = food.id,
                    actualServingSize = 1.0,
                    servingUnit = "개"
                )
            )
        ).create()
        dietDomainService.create(command)

        // when
        val diets = dietDomainService.findByUserUuidAndDate(user.uuid, mealDate)

        // then
        assertThat(diets).hasSize(1)
        assertThat(diets[0].userId).isEqualTo(user.id)
        assertThat(diets[0].dietFoods).hasSize(1)
    }

    @Test
    fun `검색 명령으로 식사 기록을 조회할 수 있다`() {
        // given
        val user = userRepository.save(UserFixture().create())
        val food = foodRepository.save(FoodFixture(name = "검색 테스트").create())
        val mealDate = LocalDate.of(2024, 3, 15) // 다른 날짜 사용
        
        val command = DietCommandFixture(
            userId = user.id,
            mealDate = mealDate,
            consumedAt = mealDate.atTime(8, 0),
            foods = mutableListOf(
                DietCommand.AddFood(
                    foodId = food.id,
                    actualServingSize = 1.0,
                    servingUnit = "개"
                )
            )
        ).create()
        dietDomainService.create(command)

        // when - userId로 검색
        val searchCommand1 = DietCommand.Search(userId = user.id, mealDate = mealDate)
        val diets1 = dietDomainService.search(searchCommand1)
        
        // when - userUuid로 검색
        val searchCommand2 = DietCommand.Search(userUuid = user.uuid, mealDate = mealDate)
        val diets2 = dietDomainService.search(searchCommand2)

        // then
        assertThat(diets1).hasSize(1)
        assertThat(diets1[0].userId).isEqualTo(user.id)
        
        assertThat(diets2).hasSize(1)
        assertThat(diets2[0].userId).isEqualTo(user.id)
    }

    @Test
    fun `존재하지 않는 날짜로 조회하면 빈 리스트를 반환한다`() {
        // given
        val user = userRepository.save(UserFixture().create())
        val nonExistentDate = LocalDate.of(2099, 12, 31)

        // when
        val diets = dietDomainService.findByUserIdAndDate(user.id, nonExistentDate)

        // then
        assertThat(diets).isEmpty()
    }
}