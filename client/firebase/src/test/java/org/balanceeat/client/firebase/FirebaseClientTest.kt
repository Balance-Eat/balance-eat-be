package org.balanceeat.client.firebase

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@Disabled
@SpringBootTest
@ActiveProfiles("manual-test")
class FirebaseClientTest {
    @Autowired
    private lateinit var firebaseClient: FirebaseClient

    @Test
    fun manualTest() {
        // NOTE: 변수 변경해서 테스트 필요
        val request = FirebaseRequest.SendMessage(
            title = "테스트 제목",
            content = "테스트 내용",
            deviceToken = "테스트 토큰", // NOTE: 실제 디바이스 토큰으로 변경
            deepLink = "balanceeat://reminder"
        )

        firebaseClient.sendMessage(request)
    }
}