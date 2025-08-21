package org.balanceeat.domain.diet

import org.assertj.core.api.Assertions.assertThat
import org.balanceeat.domain.config.supports.IntegrationTestContext
import org.balanceeat.domain.food.FoodDomainService
import org.balanceeat.domain.food.FoodRepository
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

}