package org.balanceeat.domain.food

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class FoodTest {

    @Nested
    @DisplayName("Food 엔티티의 영양성분 계산 테스트")
    inner class CalculateNutritionTest {
        @Test
        fun success() {
            // given
            val food = foodFixture {
                servingSize = 100.0
                unit = "g"
                carbohydrates = 20.0
                protein = 5.0
                fat = 2.0
            }

            // when
            val nutrition = food.calculateNutrition(200.0) // 2배 섭취

            // then
            assertEquals(40.0, nutrition.carbohydrates) // 20 * 2
            assertEquals(10.0, nutrition.protein) // 5 * 2
            assertEquals(4.0, nutrition.fat) // 2 * 2
            assertEquals(236.0, nutrition.calories) // (20*4 + 5*4 + 2*9) * 2 = 118 * 2
        }
    }

    @Nested
    @DisplayName("Food 엔티티의 업데이트 테스트")
    inner class UpdateTest {
        @Test
        fun `작성자는 음식을 업데이트할 수 있다`() {
            // given
            val originalUserId = 1L
            val food = foodFixture {
                name = "수정 전 음식"
                userId = originalUserId
                servingSize = 100.0
                unit = "g"
                carbohydrates = 20.0
                protein = 5.0
                fat = 2.0
                isAdminApproved = false
            }

            // when
            food.update(
                name = "수정 후 음식",
                servingSize = 150.0,
                unit = "ml",
                carbohydrates = 30.0,
                protein = 7.0,
                fat = 3.0,
                brand = "수정된 브랜드"
            )

            // then
            assertEquals("수정 후 음식", food.name)
            assertEquals(150.0, food.servingSize)
            assertEquals("ml", food.unit)
            assertEquals(30.0, food.carbohydrates)
            assertEquals(7.0, food.protein)
            assertEquals(3.0, food.fat)
            assertEquals(false, food.isAdminApproved) // update 메서드로는 관리자 승인 변경 불가
            // 변경되지 않아야 하는 값
            assertEquals(originalUserId, food.userId)
        }

        @Test
        fun `엔티티 업데이트는 비즈니스 규칙을 검증한다`() {
            // given
            val food = foodFixture {
                name = "기존 음식"
                servingSize = 100.0
            }

            // when & then - 잘못된 값으로 업데이트 시도
            val ex = assertThrows(IllegalArgumentException::class.java) {
                food.update(
                    name = "", // 빈 이름
                    servingSize = food.servingSize,
                    unit = food.unit,
                    carbohydrates = food.carbohydrates,
                    protein = food.protein,
                    fat = food.fat,
                    brand = food.brand
                )
                food.guard() // 엔티티 검증 호출
            }

            assertEquals("음식명은 필수값입니다", ex.message)
        }
    }
}