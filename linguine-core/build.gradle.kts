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
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.6.2")
    testImplementation("io.mockk:mockk:1.12.0")
}

gradlePlugin {
    // Define the plugin
    val linguine by plugins.creating {
        id = "com.qinshift.linguine"
        implementationClass = "com.qinshift.LinguineCore"
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
    val functionalTest by creating {
        compileClasspath += main.output
        runtimeClasspath += main.output
        kotlin.srcDir("src/functionalTest/kotlin")
        resources.srcDir("src/functionalTest/resources")
    }
}

tasks.test {
    useJUnitPlatform()
}

// Add a source set and a task for a functional test suite
gradlePlugin.testSourceSets(sourceSets["functionalTest"])

configurations[sourceSets["functionalTest"].implementationConfigurationName].extendsFrom(configurations.testImplementation.get())

val functionalTestTask = tasks.register<Test>("functionalTest") {
    useJUnitPlatform()
    testClassesDirs = sourceSets["functionalTest"].output.classesDirs
    classpath = sourceSets["functionalTest"].runtimeClasspath + sourceSets["main"].output
}


tasks.check {
    // Run the functional tests as part of `check`
    dependsOn(functionalTestTask)
}