plugins {
    // Apply the Java Gradle localisation development localisation to add support for developing Gradle plugins
    kotlin("multiplatform") version "1.9.20"
    kotlin("plugin.serialization") version "1.9.20"
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
    iosX64("ios") {
        binaries {
            framework {
                baseName = "LocalisationFramework"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
                implementation("io.github.microutils:kotlin-logging-jvm:2.0.11")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }
        val iosMain by getting {
            dependencies {
                implementation("org.example:ios-library:1.0.0")
            }
        }
    }
}

repositories {
    // Use Maven Central for resolving dependencies
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
}


/* FROM EFEKTA
plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp") version "1.5.30-1.0.0"
    id("com.qinshift.linguine") version "0.1.0-SNAPSHOT"
}

linguineConfig {
    inputFilePath = "src/commonMain/resources/strings.json"
    outputFilePath = "$projectDir/presentation"
    outputFileName = "Strings.kt"
    majorDelimiter = "__"
    minorDelimiter = "_"
}

apply<ModuleLibrary>()
apply<ConfigSerialization>()

kotlin {
    sourceSets["commonMain"].dependencies {
        implementation(projects.shared.library.logger)
    }
}

 */