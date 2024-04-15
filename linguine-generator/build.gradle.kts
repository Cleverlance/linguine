plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.gson)
    implementation(kotlin("stdlib"))
    testImplementation(gradleTestKit())
    testImplementation(kotlin("test-junit5"))
    testImplementation(libs.kotest.assertions.core.jvm)
    testImplementation(libs.mockk)
}

gradlePlugin {
    // Define the plugin
    val linguine by plugins.creating {
        id = "com.qinshift.linguine"
        implementationClass = "com.qinshift.linguine.linguinegenerator.LinguinePlugin"
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

configurations[sourceSets["functionalTest"].implementationConfigurationName]
    .extendsFrom(configurations.testImplementation.get())

val functionalTestTask = tasks.register<Test>("functionalTest") {
    useJUnitPlatform()
    testClassesDirs = sourceSets["functionalTest"].output.classesDirs
    classpath = sourceSets["functionalTest"].runtimeClasspath + sourceSets["main"].output
}

tasks.check {
    // Run the functional tests as part of `check`
    dependsOn(functionalTestTask)
}
