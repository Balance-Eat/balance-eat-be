package org.balanceeat.domain.common

import org.balanceeat.domain.config.BaseEntity

interface EntityMapper<E: BaseEntity, D> {
    fun from(entity: E): D
}