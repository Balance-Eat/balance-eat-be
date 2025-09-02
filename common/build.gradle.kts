plugins {
    kotlin("jvm")
    id("java-test-fixtures")
}

tasks.getByName("jar") {
    enabled = true
}