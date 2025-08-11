plugins {
    kotlin("plugin.jpa")
    kotlin("kapt")
    id("java-test-fixtures")
}

// noArg {
//    annotation("jakarta.persistence.Entity")
//    annotation("jakarta.persistence.Embeddable")
//    annotation("jakarta.persistence.MappedSuperclass")
// }
//
// allOpen {
//    annotation("jakarta.persistence.Entity")
//    annotation("jakarta.persistence.MappedSuperclass")
//    annotation("jakarta.persistence.Embeddable")
// }

dependencies {
//    val querydslVersion: String by project

//    implementation(project(":core"))
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

//    implementation("com.querydsl:querydsl-jpa:$querydslVersion:jakarta")
//    kapt("com.querydsl:querydsl-apt:$querydslVersion:jakarta")
//    kapt("jakarta.annotation:jakarta.annotation-api")
//    kapt("jakarta.persistence:jakarta.persistence-api")

    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.getByName("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}
