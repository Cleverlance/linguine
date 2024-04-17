import io.gitlab.arturbosch.detekt.Detekt

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.gradle.buildconfig) apply false
    alias(libs.plugins.detekt) apply true
    `maven-publish`
}

subprojects {
    apply<MavenPublishPlugin>()

    group = "com.qinshift.linguine"
    version = "0.1.0-SNAPSHOT"

    publishing {
        repositories {
            maven {
                url = uri("https://nexus.cleverlance.com/nexus/repository/maven-snapshots/")
                credentials {
                    username = System.getenv("NEXUS_USERNAME")
                    password = System.getenv("NEXUS_PASSWORD")
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
