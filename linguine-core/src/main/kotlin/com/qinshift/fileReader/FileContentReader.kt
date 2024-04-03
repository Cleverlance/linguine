package com.qinshift.fileReader

interface FileContentReader {
	fun readText(filePath: String): String
}