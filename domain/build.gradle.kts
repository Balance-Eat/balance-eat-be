val openfeignQuerydslVersion: String by project

plugins {
    kotlin("plugin.jpa")
    id("java-test-fixtures")
}

noArg {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

dependencies {
    implementation(project(":supports:jackson"))
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.github.f4b6a3:uuid-creator:6.1.0")
    implementation("com.vladmihalcea:hibernate-types-60:2.21.1")

    implementation("io.github.openfeign.querydsl:querydsl-jpa:$openfeignQuerydslVersion")
    ksp("io.github.openfeign.querydsl:querydsl-ksp-codegen:$openfeignQuerydslVersion")

    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:junit-jupiter:1.19.1")
    testImplementation("io.mockk:mockk:1.13.7")
    testFixturesApi("org.testcontainers:postgresql")
    testFixturesApi("org.testcontainers:junit-jupiter:1.19.1")
    testFixturesApi("io.mockk:mockk:1.13.7")
}

tasks.getByName("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}
