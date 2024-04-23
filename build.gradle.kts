import com.vanniktech.maven.publish.MavenPublishPlugin
import com.vanniktech.maven.publish.SonatypeHost
import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.dokka.gradle.DokkaPlugin

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.gradle.buildconfig) apply false
    alias(libs.plugins.gradle.maven.publish) apply true
    alias(libs.plugins.detekt) apply true
    alias(libs.plugins.dokka) apply false
}

subprojects {
    apply<DokkaPlugin>()
    apply<MavenPublishPlugin>()

    group = "io.github.cleverlance.linguine"
    version = "0.1.0"

    mavenPublishing {
        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
        signAllPublications()

        pom {
            name = "Linguine"
            description = "Simplifies the localization process in Kotlin projects."
            inceptionYear = "2024"
            url = "https://github.com/cleverlance/linguine/"
            licenses {
                license {
                    name = "The MIT License"
                    url = "https://github.com/cleverlance/linguine/blob/main/license.md"
                    distribution = "https://github.com/cleverlance/linguine/blob/main/license.md"
                }
            }
            developers {
                developer {
                    id = "RealDanie1"
                    name = "Daniel Pecuch"
                    url = "https://github.com/RealDanie1"
                }
                developer {
                    id = "RadekKuzel"
                    name = "Radek Kůžel"
                    url = "https://github.com/RadekKuzel"
                }
                developer {
                    id = "JiriHromek"
                    name = "Jiří Hromek"
                    url = "https://github.com/JiriHromek"
                }
                developer {
                    id = "gerak-cz"
                    name = "Bořek Leikep"
                    url = "https://github.com/gerak-cz"
                }
            }
            scm {
                url = "https://github.com/cleverlance/linguine/"
                connection = "scm:git:git://github.com/cleverlance/linguine.git"
                developerConnection = "scm:git:ssh://git@github.com:cleverlance/linguine.git"
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
