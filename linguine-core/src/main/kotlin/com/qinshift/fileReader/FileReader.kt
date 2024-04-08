package com.qinshift.fileReader

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class FileReader() {
    fun read(file: File, fileType: FileType): Map<String, String> {
        return when (fileType) {
            FileType.JSON -> parseJSON(file.readText())
        }
    }

    private fun parseJSON(fileContent: String): Map<String, String> {
        val type = object : TypeToken<Map<String, String>>() {}.type
        return Gson().fromJson(fileContent, type)
    }
}