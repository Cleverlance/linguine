package com.qinshift.linguine.linguinegenerator

import com.qinshift.linguine.linguinegenerator.filereader.FileType as LinguineFileType
import org.gradle.api.provider.Property as GradleProperty
import com.qinshift.linguine.linguine_generator.BuildConfig
import com.qinshift.linguine.linguinegenerator.filereader.FileReader
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
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
        val extension = project.extensions.create("linguine", Linguine::class.java)

        val isKmp = project.hasAnyPlugin("org.jetbrains.kotlin.multiplatform")
        val isAndroid = project.hasAnyPlugin("com.android.application", "com.android.library")
        val isJava = project.hasAnyPlugin("org.jetbrains.kotlin.jvm", "java")

        when {
            isKmp -> configureForKmp(project)
            isAndroid -> configureForAndroid(project)
            isJava -> configureForJvm(project)
        }

        project.tasks.register(GENERATE_STRINGS_TASK_NAME, GenerateStringsTask::class.java) {
            inputFile.set(project.layout.projectDirectory.file(extension.inputFilePath))
            fileType.set(extension.inputFileType)
            majorDelimiter.set(extension.majorDelimiter)
            minorDelimiter.set(extension.minorDelimiter)
            outputDirectory.set(project.layout.projectDirectory.dir(extension.outputFilePath))
            sourceRootPath.set(extension.sourceRootPath)
            outputFilePath.set(extension.outputFilePath)
            outputSuffix.set(extension.outputSuffix)
        }

        project.afterEvaluate {
            val buildTasks = extension.buildTaskName?.let { name -> listOf(task(name)) }
                ?: tasks.filter { task -> task.name.startsWith("compile") }

            buildTasks.forEach { task -> task.dependsOn(GENERATE_STRINGS_TASK_NAME) }
        }
    }

    private fun Project.hasAnyPlugin(vararg plugins: String): Boolean =
        plugins.any(this.plugins::hasPlugin)

    private fun configureForKmp(project: Project) {
        with(project.extensions.getByType<KotlinMultiplatformExtension>()) {
            sourceSets.commonMain.dependencies {
                implementation(RUNTIME_DEPENDENCY)
            }
        }
    }

    private fun configureForAndroid(project: Project) {
        project.dependencies {
            add("implementation", RUNTIME_DEPENDENCY)
        }
    }

    private fun configureForJvm(project: Project) {
        project.dependencies {
            add("implementation", RUNTIME_DEPENDENCY)
        }
    }

    private companion object {
        const val GENERATE_STRINGS_TASK_NAME = "generateStrings"
        const val RUNTIME_DEPENDENCY =
            "${BuildConfig.GROUP}:linguine-runtime:${BuildConfig.VERSION}"
    }
}

@CacheableTask
abstract class GenerateStringsTask @Inject constructor(
    private val layout: ProjectLayout,
) : DefaultTask() {

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

    @get:Input
    abstract val sourceRootPath: GradleProperty<String>

    @get:Input
    abstract val outputFilePath: GradleProperty<String>

    @get:Input
    abstract val outputSuffix: GradleProperty<String>

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

        val resolvedOutputPath = outputDirectory.get().asFile.toPath()

        val sourceRoot = sourceRootPath.get().takeIf { it.isNotBlank() }
            ?: outputFilePath.get()

        if (sourceRootPath.get().isBlank()) {
            logger.warn("Linguine: sourceRootPath not set. Falling back to outputFilePath: $sourceRoot")
        }

        val resolvedSourceRoot = layout.projectDirectory
            .dir(sourceRoot)
            .asFile
            .toPath()

        // Generate file content
        val fileContentGenerator = FileContentGenerator(
            sourceRoot = resolvedSourceRoot,
            outputDirectory = resolvedOutputPath,
            fileContent = fileContent,
            outputSuffix = outputSuffix.get()
        )

        val outputFileContent = fileContentGenerator.generateFileContents(groupedMap)

        // Write generated Kotlin files
        val fileWriter = FileWriter()
        outputFileContent.forEach { (filePath, content) ->
            fileWriter.writeToFile(filePath.toFile(), content)

            logger.info(
                "Linguine: File ${filePath.fileName} " +
                    "has been successfully created in the directory ${filePath.parent}",
            )
        }
    }
}
