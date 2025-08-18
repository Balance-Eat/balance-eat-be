import org.gradle.api.Project.DEFAULT_VERSION
import org.springframework.boot.gradle.tasks.bundling.BootJar

/** --- configuration functions --- */
fun getGitHash(): String {
    return runCatching {
        providers.exec {
            commandLine("git", "rev-parse", "--short", "HEAD")
        }.standardOutput.asText.get().trim()
    }.getOrElse { "init" }
}

plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring") apply false
    id("java-test-fixtures")
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management") apply false
    id("org.jlleitschuh.gradle.ktlint") apply false
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

kotlin {
    compilerOptions {
        jvmToolchain(21)
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allprojects {
    val projectGroup: String by project
    group = projectGroup
    version = if (version == DEFAULT_VERSION) getGitHash() else version

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply {
        plugin("java")
        plugin("io.spring.dependency-management")
        plugin("org.springframework.boot")
        plugin("org.jetbrains.kotlin.plugin.spring")
        plugin("kotlin")
        plugin("kotlin-spring")
        plugin("kotlin-kapt")
//        plugin("org.jlleitschuh.gradle.ktlint")
        plugin("java-test-fixtures")
    }

    dependencies {
        // Kotlin
        runtimeOnly("org.springframework.boot:spring-boot-starter-validation")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        // Spring
        implementation("org.springframework.boot:spring-boot-starter")
        // Serialize
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
        // Test
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
        testRuntimeOnly("org.postgresql:postgresql")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
        testImplementation("com.ninja-squad:springmockk:${project.properties["springMockkVersion"]}")
        testImplementation("org.mockito:mockito-core:${project.properties["mockitoVersion"]}")
        testImplementation("org.mockito.kotlin:mockito-kotlin:${project.properties["mockitoKotlinVersion"]}")
        testImplementation("org.instancio:instancio-junit:${project.properties["instancioJUnitVersion"]}")
        // Testcontainers
        testImplementation("org.springframework.boot:spring-boot-testcontainers")
        testImplementation("org.testcontainers:testcontainers")
        testImplementation("org.testcontainers:junit-jupiter")
        testImplementation("org.testcontainers:mysql")
        // validator
        implementation("commons-validator:commons-validator:1.7")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}