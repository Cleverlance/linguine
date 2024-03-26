plugins {
    // Apply the Java Gradle plugin development plugin to add support for developing Gradle plugins
    `kotlin-dsl`
    `maven-publish`
    id("org.jetbrains.kotlin.jvm") version "1.5.21"
}

repositories {
    // Use Maven Central for resolving dependencies
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation(gradleTestKit())
    testImplementation(kotlin("test-junit5"))
    implementation(kotlin("stdlib"))

}

gradlePlugin {
    // Define the plugin
    val localisation by plugins.creating {
        id = "com.example.plugin.localisation"
        implementationClass = "com.example.plugin.LocalisationPlugin"
        version = "1.0"
    }
}

sourceSets {
    val main by getting {
        kotlin.srcDir("src/main/kotlin")
    }
    val test by getting {
        kotlin.srcDir("src/test/kotlin")
    }
}

tasks.test {
    useJUnitPlatform()
}

// Add a source set and a task for a functional test suite
val functionalTest by sourceSets.creating
gradlePlugin.testSourceSets(functionalTest)

configurations[functionalTest.implementationConfigurationName].extendsFrom(configurations.testImplementation.get())

val functionalTestTask = tasks.register<Test>("functionalTest") {
    useJUnitPlatform()
    testClassesDirs = functionalTest.output.classesDirs
    classpath = configurations[functionalTest.runtimeClasspathConfigurationName] + functionalTest.output
}

tasks.check {
    // Run the functional tests as part of `check`
    dependsOn(functionalTestTask)
}