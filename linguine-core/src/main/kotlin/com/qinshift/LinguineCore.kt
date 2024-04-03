package com.qinshift

import org.apache.tools.ant.taskdefs.Property
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File

class LinguineCore : Plugin<Project> {

    private var fileContent: Map<String, String> = emptyMap()

    override fun apply(project: Project) {

        val extension = project.extensions.create("linguineConfig", LinguineConfig::class.java)

        project.task("loc") {
            doLast {
                // Read Input File
                val fileReader = FileReader()
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
            }
        }
    }
}

//abstract class GenerateLocalizationFileTask : DefaultTask() {
//    @get:InputFile
//    @get:PathSensitive(PathSensitivity.RELATIVE)
//    abstract val inputFile: RegularFileProperty
//
//    @get:Input
//    abstract val fileType: FileType
//
//    @get:Input
//    abstract val minorDelimiter: String
//
//    @get:Input
//    abstract val majorDelimiter: String
//
//    @get:OutputFile
//    abstract val outputFile: RegularFileProperty
//
//    @TaskAction
//    fun generate() {
//        // Implementation remains as before but using properties
//        val fileContent = FileReader().read(
//            filePath = inputFile.get().asFile.absolutePath,
//            fileType = fileType
//        )
//        val fileParser = FileParser(
//            fileContent = fileContent,
//            minorDelimiter = minorDelimiter,
//            majorDelimiter = majorDelimiter
//        )
//        val root = fileParser.generateNestedMapStructureFromJSON()
//        val outputFileContent = FileContentGenerator(fileContent).generateFileContent(root = root)
//
//        outputFile.get().asFile.writeText(outputFileContent.toString())
//    }
//}