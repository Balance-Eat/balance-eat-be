package org.balanceeat.domain.diet

import org.springframework.data.jpa.repository.JpaRepository

interface DietRepository : JpaRepository<Diet, Long>, DietRepositoryCustom {
}