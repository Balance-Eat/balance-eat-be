package org.balanceeat.client.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Profile
import java.net.URI

@Configuration
@Profile("!test")
class FirebaseConfig {
    @Value("\${application.firebase.project-id}")
    lateinit var projectId: String
    @Value("\${application.firebase.private-key-id}")
    lateinit var privateKeyId: String
    @Value("\${application.firebase.private-key}")
    lateinit var privateKey: String
    @Value("\${application.firebase.client-email}")
    lateinit var clientEmail: String
    @Value("\${application.firebase.client-id}")
    lateinit var clientId: String
    @Value("\${application.firebase.token-uri}")
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