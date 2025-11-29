rootProject.name = "balance-eat"

include(
    "application:balance-eat-api",
    "application:balance-eat-admin-api",
    "application:api-base",
    "client:base",
    "client:firebase",
    "common",
    "domain",
    "supports:jackson",
    "supports:monitoring",
)

pluginManagement {
    val kotlinVersion: String by settings
    val kspVersion: String by settings
    val springBootVersion: String by settings
    val springDependencyManagementVersion: String by settings
    val ktLintPluginVersion: String by settings
    val restdocsApiSpecPluginVersion: String by settings

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "org.jetbrains.kotlin.jvm" -> useVersion(kotlinVersion)
                "org.jetbrains.kotlin.plugin.spring" -> useVersion(kotlinVersion)
                "org.jetbrains.kotlin.plugin.jpa" -> useVersion(kotlinVersion)
                "org.springframework.boot" -> useVersion(springBootVersion)
                "io.spring.dependency-management" -> useVersion(springDependencyManagementVersion)
                "org.jlleitschuh.gradle.ktlint" -> useVersion(ktLintPluginVersion)
                "com.epages.restdocs-api-spec" -> useVersion(restdocsApiSpecPluginVersion)
                "com.google.devtools.ksp" -> useVersion(kspVersion)
            }
        }
    }
}
include("client:firebase")