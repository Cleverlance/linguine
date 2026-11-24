import java.util.Properties

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "linguine"

include(":linguine-generator")
include(":linguine-runtime")

val sampleProperty = "linguine.includeSample"
val includeSample = providers.gradleProperty(sampleProperty).orNull
    ?: rootDir.resolve("gradle-local.properties").takeIf { it.isFile }?.inputStream()?.use { input ->
        Properties().apply { load(input) }.getProperty(sampleProperty)
    }

if (includeSample.toBoolean()) {
    include(":sample:composeApp")
    include(":sample:localization-plain")
    include(":sample:localization-require-all-forms")
    include(":sample:localization-require-other-only")
}
