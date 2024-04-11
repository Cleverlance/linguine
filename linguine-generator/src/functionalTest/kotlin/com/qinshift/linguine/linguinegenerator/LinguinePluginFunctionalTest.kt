package com.qinshift.linguine.linguinegenerator

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.createTempDirectory
import kotlin.test.assertTrue

class LinguinePluginFunctionalTest {

    @TempDir
    lateinit var testProjectDir: File

    private var buildSuccessOutput: String = "BUILD SUCCESSFUL"
    private var gradleBuildFileName = "build.gradle.kts"

    @Test
    fun `plugin task executes successfully`() {
        File(testProjectDir, gradleBuildFileName).apply {
            writeText(
                """
                plugins {
                    id("com.qinshift.linguine")
                }
                
                linguineConfig {
                    inputFilePath = "src/main/resources/string.json"
                    outputFilePath = "${
                    testProjectDir.absolutePath.replace(
                        '\\',
                        '/',
                    )
                }/presentation"
                    outputFileName = "Strings.kt"
                }
                """.trimIndent(),
            )
        }

        File(testProjectDir, "src/main/resources").mkdirs()
        File(testProjectDir, "src/main/resources/string.json").writeText(
            """
            {"hello_world": "Hello, World!"}
            """.trimIndent(),
        )

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withPluginClasspath()
            .withArguments("generateStrings")
            .build()

        assert(result.output.contains(buildSuccessOutput))
    }

    @Test
    fun `plugin generates expected Kotlin file from JSON configuration`() {
        testProjectDir.resolve(gradleBuildFileName).apply {
            writeText(
                """
            plugins {
                id("com.qinshift.linguine")
            }
                
            linguineConfig {
                inputFilePath = "src/main/resources/strings.json"
                outputFilePath = "${testProjectDir.absolutePath.replace('\\', '/')}/presentation"
                outputFileName = "Strings.kt"
                majorDelimiter = "__"
                minorDelimiter = "_"
            }
                """.trimIndent(),
            )
        }

        testProjectDir.resolve("src/main/resources/strings.json").apply {
            parentFile.mkdirs()
            writeText(
                """{
"activation__forgotten_password__birthdate__log_in": "Přihlásit se",
"activation__forgotten_password__birthdate__log_out": "%s %d %f %${'$'}s %${'$'}d %${'$'}f"
}
                    """,
            )
        }

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments("generateStrings")
            .withPluginClasspath()
            .build()

        assertTrue(result.output.contains(buildSuccessOutput), "Build should be successful")

        val generatedFile = File(testProjectDir, "presentation/Strings.kt")
        assertTrue(generatedFile.exists(), "Generated file should exist")
        val actualContent = generatedFile.readText()
        val expectedContent =
            """
import com.qinshift.linguine.linguineruntime.presentation.Localiser.localise

public object Strings {
    public object Activation {
        public object ForgottenPassword {
            public object Birthdate {
                public val logIn: String = localise("activation__forgotten_password__birthdate__log_in")
                public fun logOut(param0: String, param1: Int, param2: Float, param3: String, param4: Int, param5: Float): String {
                    return localise("activation__forgotten_password__birthdate__log_out", param0, param1, param2, param3, param4, param5)
                }
            }
        }
    }
}

            """.trimIndent()
        kotlin.test.assertEquals(
            expectedContent,
            actualContent,
            "The generated file content does not match the expected content.",
        )
    }

    @Test
    fun `plugin generates file at specified location with correct content`() {
        val testProjectDir = createTempDirectory().toFile()

        File(testProjectDir, "settings.gradle.kts").writeText("")

        val projectDirPath = testProjectDir.absolutePath.replace('\\', '/')

        val buildScript =
            """
        plugins {
            id("com.qinshift.linguine")
        }

        linguineConfig {
            inputFilePath = "src/main/resources/strings.json"
            outputFilePath = "$projectDirPath/presentation"
            outputFileName = "Strings.kt"
        }
            """.trimIndent()

        File(testProjectDir, gradleBuildFileName).writeText(buildScript)

        File(testProjectDir, "src/main/resources/strings.json").apply {
            parentFile.mkdirs()
            writeText(
                """
            {
                "activation__forgotten_password__birthdate__log_in": "Přihlásit se",
                "activation__forgotten_password__birthdate__log_out": "%s %d %f %${'$'}s %${'$'}d %${'$'}f"
            }
                """.trimIndent(),
            )
        }

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withPluginClasspath()
            .withArguments("generateStrings")
            .forwardOutput()
            .build()

        assertTrue(result.output.contains(buildSuccessOutput), "Build should be successful")

        assertTrue(
            result.output.contains("File Strings.kt has been successfully created in the directory"),
            "Success message was not printed",
        )

        val expectedOutputPath =
            Paths.get(testProjectDir.path, "presentation", "Strings.kt").toString()
        assertTrue(File(expectedOutputPath).exists(), "Output file should exist")
        assertTrue(
            result.output.contains(expectedOutputPath),
            "Expected output file path '$expectedOutputPath' was not found in the build output.",
        )
    }
}
