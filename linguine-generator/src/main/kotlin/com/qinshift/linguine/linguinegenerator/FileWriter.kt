package com.qinshift.linguine.linguinegenerator

import java.io.File

class FileWriter {
    fun writeToFile(outputFile: File, outputFileContent: String) {
        if (!outputFile.exists()) {
            outputFile.parentFile?.mkdirs()
            outputFile.createNewFile()
        }
        outputFile.writeText(outputFileContent)
    }
}
