package org.balanceeat.domain.curation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CurationFoodTest {
    @Nested
    @DisplayName("CurationFood 엔티티 업데이트 테스트")
    inner class UpdateTest {
        
        @Test
        fun `가중치를 업데이트할 수 있다`() {
            // given
            val curationFood = curationFoodFixture {
                weight = 50
            }

            val newWeight = 200

            // when
            curationFood.updateWeight(newWeight)

            // then
            assertEquals(newWeight, curationFood.weight)
        }
    }
}