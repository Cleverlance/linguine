package com.qinshift.filereader

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FileReader(private val fileContentReader: FileContentReader) {
    fun read(filePath: String, fileType: FileType): Map<String, String> {
        val fileContent = fileContentReader.readText(filePath)
        return when (fileType) {
            FileType.JSON -> parseJSON(fileContent)
        }
    }

    private fun parseJSON(fileContent: String): Map<String, String> {
        val type = object : TypeToken<Map<String, String>>() {}.type
        return Gson().fromJson(fileContent, type)
    }
}