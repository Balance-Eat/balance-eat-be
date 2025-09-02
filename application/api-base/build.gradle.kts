plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":domain"))
    api(project(":supports:jackson"))
    
    // Spring Boot - API로 노출하여 사용하는 모듈에서 활용
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springframework.boot:spring-boot-starter-aop")
    
    // Logging
    api("io.github.microutils:kotlin-logging-jvm:3.0.5")

    // Swagger/OpenAPI
    api("org.springdoc:springdoc-openapi-starter-webmvc-ui:${project.properties["springDocOpenApiVersion"]}")
    
    // Optional dependencies for auto-configuration
    compileOnly("org.springframework.boot:spring-boot-autoconfigure")
    compileOnly("org.springframework.boot:spring-boot-configuration-processor")

    // Test
    testApi("org.springframework.boot:spring-boot-starter-test")
    testApi("org.springframework.boot:spring-boot-testcontainers")
    testApi("org.testcontainers:junit-jupiter")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// jar 빌드 활성화 (라이브러리 모듈이므로)
tasks.getByName("jar") {
    enabled = true
}

// bootJar 비활성화 (실행 가능한 애플리케이션이 아님)
tasks.getByName("bootJar") {
    enabled = false
}