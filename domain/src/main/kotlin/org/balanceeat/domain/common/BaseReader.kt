package org.balanceeat.domain.common

import org.balanceeat.domain.common.exception.EntityNotFoundException
import org.balanceeat.domain.config.BaseEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull

abstract class BaseReader<E: BaseEntity, R>(
    protected val repository: JpaRepository<E, Long>,
    protected val mapper: EntityMapper<E, R>
) {
    open fun existsById(id: Long): Boolean {
        return repository.existsById(id)
    }

    open fun findById(id: Long): R? {
        return  repository.findByIdOrNull(id)
            ?.let { mapper.from(it) }
    }

    abstract fun findByIdOrThrow(id: Long): R

    protected open fun findByIdOrThrow(id: Long, status: DomainStatus): R {
        return findById(id) ?: throw EntityNotFoundException(status)
    }

    open fun findAllByIds(ids: Collection<Long>): List<R> {
        return repository.findAllById(ids)
            .map { mapper.from(it) }
    }

    open fun count(): Long {
        return repository.count()
    }
}
