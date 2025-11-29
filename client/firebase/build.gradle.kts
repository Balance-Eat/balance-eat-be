dependencies {
    implementation(project(":supports:jackson"))
    api(project(":client:base"))
    implementation("com.google.firebase:firebase-admin:9.7.0")
}

tasks.getByName("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}