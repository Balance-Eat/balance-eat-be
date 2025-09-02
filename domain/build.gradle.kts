plugins {
    kotlin("plugin.jpa")
    kotlin("kapt")
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

//    val querydslVersion: String by project
//    implementation("com.querydsl:querydsl-jpa:$querydslVersion:jakarta")
//    kapt("com.querydsl:querydsl-apt:$querydslVersion:jakarta")
//    kapt("jakarta.annotation:jakarta.annotation-api")
//    kapt("jakarta.persistence:jakarta.persistence-api")

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
