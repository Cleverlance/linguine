import io.gitlab.arturbosch.detekt.Detekt

repositories {
    mavenCentral()
}

plugins {
    alias(libs.plugins.detekt)
    kotlin("multiplatform") version "1.9.20"
}

//region Detekt
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
//endregion

//tasks.register<Delete>("clean") {
//    delete(rootProject.layout.buildDirectory)
//}

tasks.clean {
    delete(rootProject.layout.buildDirectory)
}
