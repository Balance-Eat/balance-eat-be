package org.balanceeat.domain.food

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FoodRepository : JpaRepository<Food, Long> {
    
    fun findByNameContainingIgnoreCase(name: String): List<Food>
    
    fun findByBrandAndNameContainingIgnoreCase(brand: String, name: String): List<Food>
    
    fun findByBarcode(barcode: String): Food?
    
    fun findByCategory(category: String): List<Food>
    
    fun findByIsVerified(isVerified: Boolean): List<Food>
    
    @Query("SELECT f FROM Food f WHERE f.name LIKE %:keyword% OR f.brand LIKE %:keyword% OR f.description LIKE %:keyword%")
    fun searchByKeyword(@Param("keyword") keyword: String): List<Food>
    
    @Query("SELECT f FROM Food f WHERE f.category = :category AND f.isVerified = true ORDER BY f.name")
    fun findVerifiedFoodsByCategory(@Param("category") category: String): List<Food>
}