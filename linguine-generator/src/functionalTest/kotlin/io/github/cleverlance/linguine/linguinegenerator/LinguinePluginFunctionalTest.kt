package io.github.cleverlance.linguine.linguinegenerator

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

    private val generateTaskName = "generateStrings"
    private val buildSuccessOutput: String = "BUILD SUCCESSFUL"
    private val gradleBuildFileName = "build.gradle.kts"

    @Test
    fun whenGenerateTaskExecutedThenCompletedSuccessfully() {
        File(testProjectDir, gradleBuildFileName).apply {
            writeText(
                """
                plugins {
                    id("io.github.cleverlance.linguine")
                }

                linguineConfig {
                    inputFilePath = "src/main/resources/string.json"
                    outputFilePath = "${'$'}{projectDir}/presentation"
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
            .withArguments(generateTaskName)
            .build()

        assert(result.output.contains(buildSuccessOutput))
    }

    @Suppress("LongMethod")
    @Test
    fun whenGenerateTaskExecutedThenOutputsFileContainsExpectedContent() {
        testProjectDir.resolve(gradleBuildFileName).apply {
            writeText(
                """
        plugins {
            id("io.github.cleverlance.linguine")
        }

        linguineConfig {
            inputFilePath = "src/main/resources/strings.json"
            outputFilePath = "${'$'}{projectDir}/src/main/kotlin/presentation"
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
            
            import io.github.cleverlance.linguine.linguineruntime.presentation.Localiser.localise
            import kotlin.Float
            import kotlin.Int
            import kotlin.String
            
            public object Activation {
                public object ForgottenPassword {
                    public object Birthdate {
                        public val logIn: String = localise("activation__forgotten_password__birthdate__log_in")
        
                        public fun logOut(
                            param1: String,
                            param2: Int,
                            param3: Float,
                            param4: String,
                            param5: Int,
                            param6: Float,
                        ): String = localise("activation__forgotten_password__birthdate__log_out", param1,
                                param2, param3, param4, param5, param6)
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
    fun whenGenerateTaskExecutedThenOutputFilePlacedInConfiguredPath() {
        val testProjectDir = createTempDirectory().toFile()

        File(testProjectDir, "settings.gradle.kts").writeText("")

        val projectDirPath = testProjectDir.absolutePath.replace('\\', '/')

        val buildScript =
            """
        plugins {
            id("io.github.cleverlance.linguine")
        }

        linguineConfig {
            inputFilePath = "src/main/resources/strings.json"
            outputFilePath = "$projectDirPath/presentation"
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
            .withArguments(generateTaskName)
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
}
