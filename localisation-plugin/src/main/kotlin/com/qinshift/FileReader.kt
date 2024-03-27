package com.qinshift

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

enum class FileType {
    JSON
}

class FileReader {
    fun read(filePath: String, fileType: FileType): Map<String, String> {
        return when (fileType) {
            FileType.JSON -> readJson(filePath)
        }
    }

    private fun readJson(filePath: String): Map<String, String> {
        val jsonFile = File(filePath)
        val type = object : TypeToken<Map<String, String>>() {}.type
        return Gson().fromJson(jsonFile.readText(), type)
    }
}