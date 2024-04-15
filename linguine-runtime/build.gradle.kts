plugins {
    kotlin("multiplatform")
}

kotlin {
    iosX64 {
        binaries {
            framework {
                baseName = "LocalisationFramework"
            }
        }
    }
    iosArm64 {
        binaries {
            framework {
                baseName = "LocalisationFramework"
            }
        }
    }
    iosSimulatorArm64 {
        binaries {
            framework {
                baseName = "LocalisationFramework"
            }
        }
    }
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kermit)
        }
    }
}
