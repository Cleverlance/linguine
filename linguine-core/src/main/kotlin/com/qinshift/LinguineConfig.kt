package com.qinshift

import com.qinshift.filereader.FileType

open class LinguineConfig {
    var jsonFilePath: String = ""
    var fileType: FileType = FileType.JSON
    var outputDirPath: String = ""
    var stringsFileName: String = ""
    var majorDelimiter: String = "__"
    var minorDelimiter: String = "_"
}