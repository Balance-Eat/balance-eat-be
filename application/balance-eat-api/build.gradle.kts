import groovy.lang.Closure
import io.swagger.v3.oas.models.servers.Server
import org.gradle.internal.declarativedsl.intrinsics.listOf

plugins {
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
//    id("org.jlleitschuh.gradle.ktlint")
    id("java-test-fixtures")
    id("com.epages.restdocs-api-spec")
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
    implementation(project(":application:api-base"))
    implementation(project(":supports:monitoring"))

    // Test
    testImplementation(project(":application:api-base"))
    testImplementation(testFixtures(project(":domain")))
    testImplementation(sourceSets.main.get().output)
    testFixturesImplementation(sourceSets.main.get().output)
}

tasks.getByName("bootJar") {
    enabled = true
}

tasks.getByName("jar") {
    enabled = false
}

val testForApiDocs by tasks.registering(Test::class) {
    useJUnitPlatform()

    // 이 태스크는 ControllerTest만 실행하도록 필터링
    filter {
        includePatterns.add("**/*ControllerTest.kt")
    }
}

//tasks.named("openapi3") {
//
//}


openapi3 {
    mkdir("src/main/resources/static/api-docs/balanceeat")
    val outputDir = "src/main/resources/static/api-docs/balanceeat"

    title = "BalanceEat API 명세서"  // 문서 제목
    description = "BalanceEat API 명세서입니다."  // 문서 설명
    version = "1.0.0"                  // API 버전
    format = "yaml"                    // 출력 포맷 (json 또는 yaml)
    outputDirectory = outputDir // 명세 파일이 생성될 위치
    outputFileNamePrefix = "main"     // 파일명 접두사
}
