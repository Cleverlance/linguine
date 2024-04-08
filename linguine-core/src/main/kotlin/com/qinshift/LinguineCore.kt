package com.qinshift

import com.qinshift.fileReader.FileContentReaderImpl
import com.qinshift.fileReader.FileReader
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class LinguineCore : Plugin<Project> {

    private var fileContent: Map<String, String> = emptyMap()

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

        project.task("loc") {
            doLast {
                // Read Input File
                val fileReader = FileReader(FileContentReaderImpl())
                fileContent = fileReader.read(
                    filePath = "${project.projectDir}${File.separator}${extension.jsonFilePath}",
                    fileType = extension.fileType
                )

                // Parse File into internal nested object structure
                val fileParser = FileParser(
                    fileContent = fileContent,
                    minorDelimiter = extension.minorDelimiter,
                    majorDelimiter = extension.majorDelimiter
                )
                val root = fileParser.generateNestedMapStructureFromJSON()

                // Generate content for the Kotlin Localization File
                val fileContentGenerator = FileContentGenerator(fileContent)
                val outputFileContent = fileContentGenerator.generateFileContent(root = root)

                // Write built kotlin class and its nested structure into Kotlin File
                val fileWriter = FileWriter()
                fileWriter.writeToFile(
                    outputFilePath = "${extension.outputDirPath}/${extension.stringsFileName}",
                    outputFileContent = outputFileContent
                )
                println("File ${extension.stringsFileName} was successfully generated to directory ${extension.outputDirPath}")
            }
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