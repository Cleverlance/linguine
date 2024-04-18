import io.gitlab.arturbosch.detekt.Detekt

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.gradle.buildconfig) apply false
    alias(libs.plugins.detekt) apply true
    `maven-publish`
}

subprojects {
    apply<MavenPublishPlugin>()

    val majorVersion = System.getenv("MAJOR_VERSION") ?: "0"
    val minorVersion = System.getenv("MINOR_VERSION") ?: "1"
    val patchVersion = System.getenv("PATCH_VERSION") ?: "0"

    group = "com.qinshift.linguine"
    version = "$majorVersion.$minorVersion.$patchVersion-SNAPSHOT"

    publishing {
        repositories {
            maven {
                url = uri("https://nexus.cleverlance.com/nexus/repository/maven-snapshots/")
                credentials {
                    username = System.getenv("CLEVERLANCE_NEXUS_USERNAME")
                    password = System.getenv("CLEVERLANCE_NEXUS_PASSWORD")
                }
            }
        }
    }
}

// region Detekt

dependencies {
    detektPlugins(libs.detekt)
}

detekt {
    source.setFrom(files("$projectDir"))
    config.setFrom(files("$projectDir/detekt.yml"))
    parallel = true
}

tasks.withType<Detekt> {
    exclude("**/*gradle.kts")
    exclude("**/build/**")
    exclude("**/buildSrc/**")
}

// endregion

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
