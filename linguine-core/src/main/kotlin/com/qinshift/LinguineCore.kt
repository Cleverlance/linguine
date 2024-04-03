package com.qinshift

import org.gradle.api.Plugin
import org.gradle.api.Project
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