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

    group = "com.qinshift.linguine"
    version = System.getenv("NEXT_VERSION") ?: "0.3.1"

    mavenPublishing {
        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
        signAllPublications()

        pom {
            name.set("Linguine")
            description.set("Simplifies the localization process in Kotlin projects.")
            inceptionYear.set("2024")
            url.set("https://github.com/cleverlance/linguine/")
            licenses {
                license {
                    name.set("The MIT License")
                    url.set("https://github.com/cleverlance/linguine/blob/main/license.md")
                    distribution.set("https://github.com/cleverlance/linguine/blob/main/license.md")
                }
            }
            developers {
                developer {
                    id.set("RealDanie1")
                    name.set("Daniel Pecuch")
                    url.set("https://github.com/RealDanie1")
                }
                developer {
                    id.set("RadekKuzel")
                    name.set("Radek Kůžel")
                    url.set("https://github.com/RadekKuzel")
                }
                developer {
                    id.set("JiriHromek")
                    name.set("Jiří Hromek")
                    url.set("https://github.com/JiriHromek")
                }
                developer {
                    id.set("gerak-cz")
                    name.set("Bořek Leikep")
                    url.set("https://github.com/gerak-cz")
                }
            }
            scm {
                url.set("https://github.com/cleverlance/linguine/")
                connection.set("scm:git:git://github.com/cleverlance/linguine.git")
                developerConnection.set("scm:git:ssh://git@github.com:cleverlance/linguine.git")
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
