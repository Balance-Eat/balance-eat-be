package org.balanceeat.common

interface TestFixture<T> {
    fun create(): T
}