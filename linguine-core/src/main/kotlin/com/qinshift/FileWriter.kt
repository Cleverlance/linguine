package com.qinshift

import java.io.File

class FileWriter {
    fun writeToFile(outputFilePath: String, outputFileContent: StringBuilder) {
        File(outputFilePath).apply {
            parentFile.mkdirs()
            writeText(outputFileContent.toString())
        }
    }
}