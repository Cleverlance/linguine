import io.gitlab.arturbosch.detekt.Detekt

repositories {
    mavenCentral()
}

plugins {
    id("io.gitlab.arturbosch.detekt") version "1.23.3"
}

//region Detekt
dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.3")
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

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
