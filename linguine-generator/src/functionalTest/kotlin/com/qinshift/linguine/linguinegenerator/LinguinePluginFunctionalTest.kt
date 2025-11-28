package com.qinshift.linguine.linguinegenerator

import java.io.File
import java.nio.file.Paths
import kotlin.io.path.createTempDirectory
import kotlin.test.assertTrue
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

@Suppress("StringLiteralDuplication")
class LinguinePluginFunctionalTest {

    @TempDir
    lateinit var testProjectDir: File

    private val generateTaskName = "generateStrings"
    private val buildSuccessOutput = "BUILD SUCCESSFUL"
    private val gradleBuildFileName = "build.gradle.kts"

    @Test
    fun whenGenerateTaskExecutedThenCompletedSuccessfully() {
        File(testProjectDir, gradleBuildFileName).apply {
            writeText(
                """
                plugins {
                    id("com.qinshift.linguine")
                }

                linguine {
                    inputFilePath = "src/main/resources/string.json"
                    outputFilePath = "${'$'}{projectDir}/presentation"
                    sourceRootPath = "presentation"
                }
                """.trimIndent(),
            )
        }

        File(testProjectDir, "src/main/resources").mkdirs()
        File(testProjectDir, "src/main/resources/string.json").writeText(
            """{"hello_world": "Hello, World!"}""",
        )

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withPluginClasspath()
            .withArguments(generateTaskName)
            .build()

        assertTrue(result.output.contains(buildSuccessOutput))
    }

    @Suppress("LongMethod")
    @Test
    fun whenGenerateTaskExecutedThenOutputsFileContainsExpectedContent() {
        testProjectDir.resolve(gradleBuildFileName).apply {
            writeText(
                """
                plugins {
                    id("com.qinshift.linguine")
                }

                linguine {
                    inputFilePath = "src/main/resources/strings.json"
                    outputFilePath = "src/main/kotlin/presentation"
                    sourceRootPath = "src/main/kotlin"
                    majorDelimiter = "__"
                    minorDelimiter = "_"
                }
                """.trimIndent(),
            )
        }

        testProjectDir.resolve("src/main/resources/strings.json").apply {
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
            .withArguments(generateTaskName)
            .withPluginClasspath()
            .forwardOutput()
            .build()

        println(result.output)

        assertTrue(result.output.contains(buildSuccessOutput), "Build should be successful")

        val generatedFile =
            File(testProjectDir, "src/main/kotlin/presentation/ActivationStrings.kt")
        assertTrue(generatedFile.exists(), "Generated file should exist")

        val actualContent = generatedFile.readText()
        val expectedContent = """
            package presentation
            
            import com.qinshift.linguine.linguineruntime.presentation.Localiser.localise
            import kotlin.Float
            import kotlin.Int
            import kotlin.String
            
            public object ActivationStrings {
                public object ForgottenPassword {
                    public object Birthdate {
                        public val logIn: String =
                                localise("activation__forgotten_password__birthdate__log_in")
        
                        public fun logOut(
                            param1: String,
                            param2: Int,
                            param3: Float,
                            param4: String,
                            param5: Int,
                            param6: Float,
                        ): String = localise("activation__forgotten_password__birthdate__log_out",
                                param1, param2, param3, param4, param5, param6)
                    }
                }
            }
        """.trimIndent()

        kotlin.test.assertEquals(
            normalizeWhitespace(expectedContent),
            normalizeWhitespace(actualContent),
            "The generated file content does not match the expected structure.",
        )
    }

    @Test
    fun whenGenerateTaskExecutedThenOutputFilePlacedInConfiguredPath() {
        val testProjectDir = createTempDirectory().toFile()
        File(testProjectDir, "settings.gradle.kts").writeText("")

        val projectDirPath = testProjectDir.absolutePath.replace('\\', '/')

        val buildScript = """
            plugins {
                id("com.qinshift.linguine")
            }

            linguine {
                inputFilePath = "src/main/resources/strings.json"
                outputFilePath = "$projectDirPath/presentation"
                sourceRootPath = "$projectDirPath"
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
            .withArguments(generateTaskName, "--info")
            .forwardOutput()
            .build()

        assertTrue(result.output.contains(buildSuccessOutput), "Build should be successful")

        val expectedSuccessMessagePart =
            "File ActivationStrings.kt has been successfully created in the directory"
        assertTrue(
            result.output.contains(expectedSuccessMessagePart),
            "Success message was not printed correctly",
        )

        val expectedOutputPath =
            Paths.get(testProjectDir.path, "presentation", "ActivationStrings.kt").toString()
                .replace('\\', '/')
        assertTrue(File(expectedOutputPath).exists(), "Output file should exist")
        val outputPathComponents = expectedOutputPath.split('/')
        outputPathComponents.forEach { component ->
            assertTrue(
                result.output.contains(component),
                "Expected output path component '$component' was not found in the build output.",
            )
        }
    }

    @Test
    fun whenCustomOutputSuffixConfiguredThenFileAndRootObjectUseSuffix() {
        testProjectDir.resolve(gradleBuildFileName).apply {
            writeText(
                """
                plugins {
                    id("com.qinshift.linguine")
                }

                linguine {
                    inputFilePath = "src/main/resources/strings.json"
                    outputFilePath = "src/main/kotlin/presentation"
                    sourceRootPath = "src/main/kotlin"
                    outputSuffix = "L10n"
                }
                """.trimIndent(),
            )
        }

        testProjectDir.resolve("src/main/resources/strings.json").apply {
            parentFile.mkdirs()
            writeText(
                """
                {
                    "activation__forgotten_password__birthdate__log_in": "Přihlásit se"
                }
                """.trimIndent(),
            )
        }

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(generateTaskName)
            .withPluginClasspath()
            .forwardOutput()
            .build()

        assertTrue(result.output.contains(buildSuccessOutput), "Build should be successful")

        val generatedFile =
            File(testProjectDir, "src/main/kotlin/presentation/ActivationL10n.kt")
        assertTrue(generatedFile.exists(), "Generated file with custom suffix should exist")

        val actualContent = generatedFile.readText()

        assertTrue(
            actualContent.contains("public object ActivationL10n"),
            "Expected root object 'ActivationL10n' in generated content, but was:\n$actualContent",
        )
        assertTrue(
            actualContent.contains(
                """public val logIn: String = localise("activation__forgotten_password__birthdate__log_in")"""
                    .trimIndent(),
            ),
            "Expected property 'logIn' in generated content, but was:\n$actualContent",
        )
    }

    private fun normalizeWhitespace(code: String): String =
        code.trimIndent().replace(Regex("\\s+"), " ").trim()
}