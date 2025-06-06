package com.qinshift.linguine.linguinegenerator

import com.qinshift.linguine.linguinegenerator.filereader.FileType as LinguineFileType
import org.gradle.api.provider.Property as GradleProperty
import com.qinshift.linguine.linguine_generator.BuildConfig
import com.qinshift.linguine.linguinegenerator.filereader.FileReader
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.work.Incremental
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class LinguinePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create("linguineConfig", LinguineConfig::class.java)

        val isKmp = project.hasAnyPlugin("org.jetbrains.kotlin.multiplatform")
        val isAndroid = project.hasAnyPlugin("com.android.application", "com.android.library")
        val isJava = project.hasAnyPlugin("org.jetbrains.kotlin.jvm", "java")

        when {
            isKmp -> configureForKmp(project, extension)
            isAndroid -> configureForAndroid(project, extension)
            isJava -> configureForJvm(project, extension)
        }

        project.tasks.register(GENERATE_STRINGS_TASK_NAME, GenerateStringsTask::class.java) {
            inputFile.set(project.layout.projectDirectory.file(extension.inputFilePath))
            fileType.set(extension.inputFileType)
            majorDelimiter.set(extension.majorDelimiter)
            minorDelimiter.set(extension.minorDelimiter)
            outputDirectory.set(
                project.layout.projectDirectory.dir(
                    extension.outputFilePath,
                ),
            )
        }
    }

    private fun Project.hasAnyPlugin(vararg plugins: String): Boolean =
        plugins.any(this.plugins::hasPlugin)

    private fun configureForKmp(project: Project, extension: LinguineConfig) {
        with(project.extensions.getByType<KotlinMultiplatformExtension>()) {
            sourceSets.commonMain.dependencies {
                implementation(RUNTIME_DEPENDENCY)
            }
        }
        configureGenerateStringsTask(project, extension)
    }

    private fun configureForAndroid(project: Project, extension: LinguineConfig) {
        project.dependencies {
            add("implementation", RUNTIME_DEPENDENCY)
        }
        configureGenerateStringsTask(project, extension)
    }

    private fun configureForJvm(project: Project, extension: LinguineConfig) {
        project.dependencies {
            add("implementation", RUNTIME_DEPENDENCY)
        }
        configureGenerateStringsTask(project, extension)
    }

    private fun configureGenerateStringsTask(project: Project, extension: LinguineConfig) {
        project.afterEvaluate {
            val buildTasks = extension.buildTaskName?.let { name -> listOf(task(name)) }
                ?: tasks.filter { task -> task.name.startsWith("compile") }

            buildTasks.forEach { task -> task.dependsOn(GENERATE_STRINGS_TASK_NAME) }
        }
    }

    private companion object {
        const val GENERATE_STRINGS_TASK_NAME = "generateStrings"
        const val RUNTIME_DEPENDENCY =
            "${BuildConfig.GROUP}:linguine-runtime:${BuildConfig.VERSION}"
    }
}

@CacheableTask
abstract class GenerateStringsTask : DefaultTask() {
    @get:Incremental
    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputFile: RegularFileProperty

    @get:Input
    abstract val fileType: GradleProperty<LinguineFileType>

    @get:Input
    abstract val minorDelimiter: GradleProperty<String>

    @get:Input
    abstract val majorDelimiter: GradleProperty<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        // Read input file
        val fileContent = FileReader().read(
            file = inputFile.asFile.get(),
            fileType = fileType.get(),
        )

        // Parse into nested object structure
        val fileParser = FileParser(
            fileContent = fileContent,
            minorDelimiter = minorDelimiter.get(),
            majorDelimiter = majorDelimiter.get(),
        )

        val groupedMap = fileParser.generateGroupedMapStructure()

        // Get config and resolve paths
        val config = project.extensions.getByType(LinguineConfig::class.java)
        val resolvedOutputPath = outputDirectory.get().asFile.toPath()

        val sourceRootPath = config.sourceRootPath
            .takeIf { it.isNotBlank() }
            ?: config.outputFilePath

        if (config.sourceRootPath.isBlank()) {
            logger.warn("Linguine: sourceRootPath not set. Falling back to outputFilePath: $sourceRootPath")
        }

        val resolvedSourceRoot = project.layout.projectDirectory
            .dir(sourceRootPath)
            .asFile
            .toPath()

        // Generate file content
        val fileContentGenerator = FileContentGenerator(
            sourceRoot = resolvedSourceRoot,
            outputDirectory = resolvedOutputPath,
            fileContent = fileContent,
        )

        val outputFileContent = fileContentGenerator.generateFileContents(groupedMap)

        // Write generated Kotlin files
        val fileWriter = FileWriter()
        outputFileContent.forEach { (filePath, content) ->
            fileWriter.writeToFile(filePath.toFile(), content)

            logger.lifecycle(
                "Linguine: File ${filePath.fileName} " +
                    "has been successfully created in the directory ${filePath.parent}",
            )
        }
    }
}
