package org.balanceeat.domain.config

import com.github.f4b6a3.uuid.UuidCreator
import java.util.UUID

object UuidGenerator {
    
    /**
     * UUID v7을 생성합니다.
     * UUID v7은 시간 기반으로 정렬 가능하며, 데이터베이스 인덱스 성능에 유리합니다.
     */
    fun generateUuidV7(): UUID {
        return UuidCreator.getTimeOrdered()
    }
    
    /**
     * 기존 호환성을 위한 UUID v4 생성 (랜덤)
     */
    fun generateUuidV4(): UUID {
        return UUID.randomUUID()
    }
    
    /**
     * UUID 문자열이 유효한지 검증합니다.
     */
    fun isValidUuid(uuid: String): Boolean {
        return try {
            UUID.fromString(uuid)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}