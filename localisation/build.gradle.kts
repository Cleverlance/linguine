plugins {
    // Apply the Java Gradle localisation development localisation to add support for developing Gradle plugins
    `kotlin-dsl`
    `maven-publish`
}

repositories {
    // Use Maven Central for resolving dependencies
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
}
