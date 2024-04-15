package com.qinshift.linguine.linguinegenerator

import com.qinshift.linguine.linguinegenerator.fileReader.FileType as LinguineFileType
import org.gradle.api.provider.Property as GradleProperty
import com.qinshift.linguine.linguinegenerator.fileReader.FileReader
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.Incremental

@Suppress("unused")
class LinguinePlugin : Plugin<Project> {

    override fun apply(project: Project) {

        val extension = project.extensions.create("linguineConfig", LinguineConfig::class.java)

        val isAndroid =
            project.plugins.hasPlugin("com.android.application") || project.plugins.hasPlugin("com.android.library")
        val isKMP = project.plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")
        val isJvm =
            project.plugins.hasPlugin("org.jetbrains.kotlin.jvm") || project.plugins.hasPlugin("java")

        when {
            isAndroid -> configureForAndroid(project, extension)
            isKMP -> configureForKMP(project, extension)
            isJvm -> configureForJvm(project, extension)
        }

        project.tasks.register("loc", LocalizeTask::class.java) {
            inputFile.set(project.layout.projectDirectory.file(extension.inputFilePath))
            fileType.set(extension.inputFileType)
            minorDelimiter.set(extension.minorDelimiter)
            majorDelimiter.set(extension.majorDelimiter)
            outputFile.set(
                project.layout.projectDirectory.file(
                    "${extension.outputFilePath}/${extension.outputFileName}",
                ),
            )
        }
    }

    private fun configureForAndroid(project: Project, extension: LinguineConfig) {
        // TODO
    }

    private fun configureForKMP(project: Project, extension: LinguineConfig) {
        project.tasks.named(extension.buildTaskName ?: "build") {
            dependsOn("loc")
        }
    }

    private fun configureForJvm(project: Project, extension: LinguineConfig) {
        // TODO
    }
}

@CacheableTask
abstract class LocalizeTask : DefaultTask() {
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

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun generate() {
        var fileContent: Map<String, String> = emptyMap()
        // Read Input File
        val fileReader = FileReader()
        fileContent = fileReader.read(
            file = inputFile.asFile.get(),
            fileType = fileType.get(),
        )

        // Parse File into internal nested object structure
        val fileParser = FileParser(
            fileContent = fileContent,
            minorDelimiter = minorDelimiter.get(),
            majorDelimiter = majorDelimiter.get(),
        )
        val root = fileParser.generateNestedMapStructureFromJSON()

        // Generate content for the Kotlin Localization File
        val fileContentGenerator = FileContentGenerator(fileContent)
        val outputFileContent = fileContentGenerator.generateFileContent(root = root)

        // Write built kotlin class and its nested structure into Kotlin File
        val fileWriter = FileWriter()
        fileWriter.writeToFile(
            outputFile = outputFile.asFile.get(),
            outputFileContent = outputFileContent,
        )
        println(
            "Linguine: File ${outputFile.asFile.get().name} " +
                "has been successfully created in the directory ${outputFile.asFile.get().path}",
        )
    }
}
