package org.balanceeat.jackson

import com.fasterxml.jackson.core.type.TypeReference
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class JsonUtilsTest {
    data class TestData(
        val name: String,
        val age: Int,
        val email: String? = null
    )

    @Test
    fun `stringify - 객체를 JSON 문자열로 변환`() {
        val data = TestData("김철수", 30, "kim@example.com")
        val result = JsonUtils.stringify(data)
        
        assertEquals("""{"name":"김철수","age":30,"email":"kim@example.com"}""", result)
    }

    @Test
    fun `stringify - null 값 제외하여 직렬화`() {
        val data = TestData("이영희", 25)
        val result = JsonUtils.stringify(data)
        
        assertEquals("""{"name":"이영희","age":25}""", result)
    }

    @Test
    fun `stringify - null 입력 시 null 문자열 반환`() {
        val result = JsonUtils.stringify(null)
        assertEquals("null", result)
    }

    @Test
    fun `parse - JSON 문자열을 객체로 변환`() {
        val json = """{"name":"박민수","age":28,"email":"park@example.com"}"""
        val result = JsonUtils.parse<TestData>(json)
        
        assertEquals("박민수", result.name)
        assertEquals(28, result.age)
        assertEquals("park@example.com", result.email)
    }

    @Test
    fun `parse - 클래스 타입으로 파싱`() {
        val json = """{"name":"최유진","age":35}"""
        val result = JsonUtils.parse(json, TestData::class.java)
        
        assertEquals("최유진", result.name)
        assertEquals(35, result.age)
        assertEquals(null, result.email)
    }

    @Test
    fun `parseToMap - JSON을 Map으로 변환`() {
        val json = """{"name":"홍길동","age":40,"active":true}"""
        val result = JsonUtils.parseToMap(json)
        
        assertEquals("홍길동", result["name"])
        assertEquals(40, result["age"])
        assertEquals(true, result["active"])
    }

    @Test
    fun `parseToList - JSON 배열을 List로 변환`() {
        val json = """[{"name":"사용자1"},{"name":"사용자2"}]"""
        val result = JsonUtils.parseToList(json)
        
        assertEquals(2, result.size)
        assertNotNull(result[0])
        assertNotNull(result[1])
    }

    @Test
    fun `parse - TypeReference로 제네릭 타입 파싱`() {
        val json = """[{"name":"김사용자","age":25},{"name":"이사용자","age":30}]"""
        val typeRef = object : TypeReference<List<TestData>>() {}
        val result = JsonUtils.parse(json, typeRef)
        
        assertEquals(2, result.size)
        assertEquals("김사용자", result[0].name)
        assertEquals("이사용자", result[1].name)
    }

    @Test
    fun `parse - 잘못된 JSON 형식 시 예외 발생`() {
        val invalidJson = """{"name":"잘못된JSON"""
        
        assertThrows<Exception> {
            JsonUtils.parse<TestData>(invalidJson)
        }
    }
}