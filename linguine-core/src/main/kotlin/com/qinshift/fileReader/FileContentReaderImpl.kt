package com.qinshift.fileReader

import java.io.File

class FileContentReaderImpl: FileContentReader {
	override fun readText(filePath: String): String {
		return File(filePath).readText()
	}
}