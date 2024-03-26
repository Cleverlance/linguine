package com.qinshift

import org.gradle.internal.impldep.junit.framework.TestCase.assertEquals
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.assertTrue

class LocalisationPluginFunctionalTest {

    @TempDir
    lateinit var testProjectDir: File

    @Test
    fun canRunTask() {
        // Setup the test build
        val buildFile = File(testProjectDir, "build.gradle.kts").apply {
            writeText(
                """
                plugins {
                    id("com.qinshift.linguine")
                }
                
                localisation {
                    jsonFilePath = "src/main/resources/string.json"
                 outputDirPath = "${testProjectDir.absolutePath.replace('\\', '/')}/presentation"
                    stringsFileName = "Strings.kt"
                }
            """.trimIndent()
            )
        }

        // Create a sample JSON file to satisfy the plugin's expectations
        File(testProjectDir, "src/main/resources").mkdirs()
        File(testProjectDir, "src/main/resources/string.json").writeText(
            """
            {"hello_world": "Hello, World!"}
        """.trimIndent()
        )

        // Run the build
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withPluginClasspath()
            .withArguments("loc")
            .build()

        // Assertions to ensure task executed successfully
        assert(result.output.contains("BUILD SUCCESSFUL"))
    }

    @Test
    fun `plugin generates correct Kotlin file from JSON`() {
        // Setup the build file
        val buildFile = testProjectDir.resolve("build.gradle.kts").apply {
            writeText(
                """
            plugins {
                id("com.qinshift.linguine")
            }
                
            configure<com.qinshift.LocalisationExtension> {
                jsonFilePath = "/src/main/resources/strings.json"
                outputDirPath = "$testProjectDir/presentation"
                stringsFileName = "Strings.kt"
            }
            """.trimIndent()
            )
        }

        // Create a sample JSON file
        val jsonFile = testProjectDir.resolve("src/main/resources/strings.json").apply {
            parentFile.mkdirs()
            writeText("""{"hello_world": "Hello, World!"}""")
        }

        // Run the plugin task
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments("loc")
            .withPluginClasspath()
            .build()

        // Check the results
        assertTrue(result.output.contains("BUILD SUCCESSFUL"), "Build should be successful")

        val generatedFile = File(testProjectDir, "presentation/Strings.kt")
        assertTrue(generatedFile.exists(), "Generated file should exist")
        val actualContent = generatedFile.readText()
        val expectedContent = """public object Strings {
	 public val helloWorld: String = localise("hello_world")
}

        """.trimIndent()
        kotlin.test.assertEquals(
            actualContent,
            expectedContent,
            "The generated file content does not match the expected content."
        )
    }
}
