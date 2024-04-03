package com.qinshift.filereader

interface FileContentReader {
	fun readText(filePath: String): String
}