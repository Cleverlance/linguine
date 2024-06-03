package io.github.cleverlance.linguine.linguinegenerator

import io.github.cleverlance.linguine.linguinegenerator.filereader.FileType

open class LinguineConfig {
    var inputFilePath: String = ""
    var inputFileType: FileType = FileType.JSON
    var outputFilePath: String = ""
    var majorDelimiter: String = "__"
    var minorDelimiter: String = "_"
    var buildTaskName: String? = null
}
