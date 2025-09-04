plugins {
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("kapt")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
//    id("org.jlleitschuh.gradle.ktlint")
    id("java-test-fixtures")
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
    testFixturesImplementation(project(":domain"))
    testFixturesImplementation(project(":common"))
}

tasks.getByName("bootJar") {
    enabled = true
}

tasks.getByName("jar") {
    enabled = false
}