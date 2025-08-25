package org.balanceeat.domain.config

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.util.UUID

class UuidGeneratorTest {

    @Test
    fun `UUID v7 생성 테스트`() {
        // given & when
        val uuidV7 = UuidGenerator.generateUuidV7()
        
        // then
        assertNotNull(uuidV7)
        assertTrue(UuidGenerator.isValidUuid(uuidV7.toString()))
        assertEquals(36, uuidV7.toString().length) // UUID 표준 길이
        assertTrue(uuidV7.toString().contains("-")) // UUID 형식
    }

    @Test
    fun `연속된 UUID v7들은 시간 순으로 정렬됨`() {
        // given & when
        val uuid1 = UuidGenerator.generateUuidV7()
        Thread.sleep(1) // 최소한의 시간 차이
        val uuid2 = UuidGenerator.generateUuidV7()
        
        // then
        assertTrue(uuid1.toString() < uuid2.toString()) // UUID v7은 시간 기반으로 정렬 가능
    }

    @Test
    fun `UUID v4 생성 테스트`() {
        // given & when
        val uuidV4 = UuidGenerator.generateUuidV4()
        
        // then
        assertNotNull(uuidV4)
        assertTrue(UuidGenerator.isValidUuid(uuidV4.toString()))
    }

    @Test
    fun `유효하지 않은 UUID 검증`() {
        // given
        val invalidUuid = "not-a-uuid"
        
        // when & then
        assertFalse(UuidGenerator.isValidUuid(invalidUuid))
    }

    @Test
    fun `유효한 UUID 검증`() {
        // given
        val validUuid = UUID.randomUUID().toString()
        
        // when & then
        assertTrue(UuidGenerator.isValidUuid(validUuid))
    }
}