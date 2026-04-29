package com.qinshift.linguine.linguinegenerator

import com.qinshift.linguine.linguinegenerator.filereader.FileType

public open class Linguine {
    public var inputFilePath: String = ""
    public var inputFileType: FileType = FileType.JSON
    public var outputFilePath: String = ""
    public var sourceRootPath: String = ""
    public var outputSuffix: String = "Strings"
    public var majorDelimiter: String = "__"
    public var minorDelimiter: String = "_"
    public var buildTaskName: String? = null
    public var pluralFormPolicy: PluralFormPolicy = PluralFormPolicy.REQUIRE_ALL_FORMS
}
