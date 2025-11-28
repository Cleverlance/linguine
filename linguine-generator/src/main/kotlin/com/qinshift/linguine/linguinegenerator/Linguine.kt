package com.qinshift.linguine.linguinegenerator

import com.qinshift.linguine.linguinegenerator.filereader.FileType

open class Linguine {
    var inputFilePath: String = ""
    var inputFileType: FileType = FileType.JSON
    var outputFilePath: String = ""
    var sourceRootPath: String = ""
    var outputSuffix: String = "Strings"
    var majorDelimiter: String = "__"
    var minorDelimiter: String = "_"
    var buildTaskName: String? = null
}
