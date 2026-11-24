import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
}

@CacheableTask
abstract class GenerateChecklistExpectedTask : DefaultTask() {
    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val outputDirectory = outputDir.get().asFile
        outputDirectory.deleteRecursively()

        val outputFile = outputDirectory.resolve("com/qinshift/linguine/sample/data/ChecklistExpectedRaw.kt")
        val rawJson = inputFile
            .get()
            .asFile
            .readText()
            .replace("$", "\${'$'}")
            .replace("\"\"\"", "\\\"\\\"\\\"")

        outputFile.parentFile.mkdirs()
        outputFile.writeText(
            """
            package com.qinshift.linguine.sample.data

            internal val CHECKLIST_EXPECTED_JSON: String = ""${'"'}$rawJson""${'"'}
            """.trimIndent(),
        )
    }
}

val generatedChecklistExpectedDir = layout.buildDirectory.dir("generated/checklistExpected/commonMain/kotlin")
val generateChecklistExpected = tasks.register<GenerateChecklistExpectedTask>("generateChecklistExpected") {
    inputFile.set(layout.projectDirectory.file("src/commonMain/resources/checklist/checklist_expected.json"))
    outputDir.set(generatedChecklistExpectedDir)
}

kotlin {
    androidTarget()

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "LinguineSampleShared"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(projects.linguineRuntime)
            implementation(projects.sample.localizationPlain)
            implementation(projects.sample.localizationRequireAllForms)
            implementation(projects.sample.localizationRequireOtherOnly)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        commonMain {
            kotlin.srcDir(generatedChecklistExpectedDir)
        }
    }
}

android {
    namespace = "com.qinshift.linguine.sample"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.qinshift.linguine.sample"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

tasks.matching { task -> task.name.contains("Kotlin") }.configureEach {
    dependsOn(generateChecklistExpected)
}
