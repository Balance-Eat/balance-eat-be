plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("kapt")
    id("org.jlleitschuh.gradle.ktlint")
    id("java-test-fixtures")
}

repositories {
    mavenCentral()
}

dependencies {
    // Module dependencies
    implementation(project(":domain"))
    implementation(project(":supports:jackson"))
    implementation(project(":supports:monitoring"))

    // Environment variables support (IntelliJ 호환)
    implementation("io.github.cdimascio:dotenv-java:3.0.0")

    // Logging
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = true
    mainClass.set("org.balanceeat.api.BalanceEatApiApplicationKt")
}

tasks.getByName<Jar>("jar") {
    enabled = false
}
