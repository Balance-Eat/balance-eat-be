dependencies {
    // Spring Boot
    api("org.springframework.boot:spring-boot-starter-actuator")

    // Monitoring
    api("io.micrometer:micrometer-registry-prometheus")
    api("io.micrometer:micrometer-core")

    // Logging
    api("io.github.microutils:kotlin-logging-jvm:3.0.5")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.getByName("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}
