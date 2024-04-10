package com.qinshift.linguine.plugin

import com.qinshift.linguine.plugin.fileReader.FileType

open class LinguineConfig {
    var inputFilePath: String = ""
    var inputFileType: FileType = FileType.JSON
    var outputFilePath: String = ""
    var outputFileName: String = ""
    var majorDelimiter: String = "__"
    var minorDelimiter: String = "_"
    var buildTaskName: String? = null
}
