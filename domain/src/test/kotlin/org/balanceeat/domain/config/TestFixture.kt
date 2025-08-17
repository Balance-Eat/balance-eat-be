package org.balanceeat.domain.config

interface TestFixture<T> {
    fun create(): T
}