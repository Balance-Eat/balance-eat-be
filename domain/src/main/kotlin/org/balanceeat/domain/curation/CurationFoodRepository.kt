package org.balanceeat.domain.curation

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CurationFoodRepository : JpaRepository<CurationFood, Long> {
    
    @Query("""
        SELECT cf 
        FROM CurationFood cf 
        ORDER BY cf.weight DESC
    """)
    fun findRecommendedFoods(pageable: Pageable): List<CurationFood>
}