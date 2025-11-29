package org.balanceeat.client.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import java.net.URI

@Configuration
class FirebaseConfig {
    @Value("\${firebase.project-id}")
    lateinit var projectId: String
    @Value("\${firebase.private-key-id}")
    lateinit var privateKeyId: String
    @Value("\${firebase.private-key}")
    lateinit var privateKey: String
    @Value("\${firebase.client-email}")
    lateinit var clientEmail: String
    @Value("\${firebase.client-id}")
    lateinit var clientId: String
    @Value("\${firebase.token-uri}")
    lateinit var tokenUri: String

    @Bean
    fun firebaseApp(googleCredentials: GoogleCredentials): FirebaseApp {
        val options = FirebaseOptions.builder()
            .setCredentials(googleCredentials)
            .build()

        return FirebaseApp.initializeApp(options)
    }

    @Bean
    @DependsOn("firebaseApp")
    fun firebaseMessaging(firebaseApp: FirebaseApp): FirebaseMessaging {
        return FirebaseMessaging.getInstance(firebaseApp)
    }

    @Bean
    fun googleCredentials(): GoogleCredentials {
        return ServiceAccountCredentials.newBuilder()
            .setProjectId(projectId)
            .setPrivateKeyId(privateKeyId)
            .setPrivateKeyString(privateKey.replace("\\n", "\n"))
            .setClientEmail(clientEmail)
            .setClientId(clientId)
//            .setHttpTransportFactory(NetHttpTransport())
            .setTokenServerUri(URI.create(tokenUri))
            .build()
    }
}