package com.qinshift.linguine.linguinegenerator

class FileContentGenerator(private val fileContent: Map<String, String>) {

    private companion object {
        const val DEFAULT_INDENT = "    "
        val FORMAT_SPECIFIER_REGEX = Regex("%[0-9]*\\\$[sdf]|%[sdf]")
    }

    fun generateFileContent(root: Map<String, Any>): StringBuilder {
        val stringBuilder = StringBuilder()

        stringBuilder.apply {
            append("import com.qinshift.linguine.linguineruntime.presentation.Localiser.localise\n\n")
            append("public object Strings {\n")
            generateKotlinCode(stringBuilder, root, 1)
            append("}\n")
        }
        return stringBuilder
    }

    private fun generateKotlinCode(builder: StringBuilder, map: Map<String, Any>, depth: Int) {
        map.forEach { (key, value) ->
            val indent = DEFAULT_INDENT.repeat(depth)
            if (value is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                appendKotlinObject(builder, key, value as Map<String, Any>, depth, indent)
            } else {
                appendFunctionOrValue(builder, key, value.toString(), indent)
            }
        }
    }

    private fun appendKotlinObject(
        builder: StringBuilder,
        key: String,
        value: Map<String, Any>,
        depth: Int,
        indent: String,
    ) {
        builder.apply {
            append("${indent}public object $key {\n")
            generateKotlinCode(this, value, depth + 1)
            append("$indent}\n")
        }
    }

    private fun appendFunctionOrValue(
        builder: StringBuilder,
        key: String,
        value: String,
        indent: String,
    ) {
        val translation = fileContent.filter { it.key == value }.toString()
        val dataTypes = determineDataTypes(translation)

        if (dataTypes.isNotEmpty()) {
            appendFunctionDeclaration(builder, key, value, dataTypes, indent)
        } else {
            val validName = returnValidValName(key)
            builder.append("${indent}public val $validName: String = localise(\"$value\")\n")
        }
    }

    private fun returnValidValName(key: String): String {
        if (key == "continue") {
            return "`continue`"
        } else return key
    }

    private fun appendFunctionDeclaration(
        builder: StringBuilder,
        key: String,
        value: String,
        dataTypes: List<String>,
        indent: String,
    ) {
        builder.apply {
            append("${indent}public fun $key(")
            dataTypes.forEachIndexed { index, type ->
                if (index > 0) append(", ")
                append("param$index: $type")
            }
            append("): String {\n")
            append("${indent}${DEFAULT_INDENT}return localise(\"$value\", ")
            append(dataTypes.indices.joinToString { "param$it" })
            append(")\n$indent}\n")
        }
    }

    // %s - valid parameter, can be without $
    private fun determineDataTypes(formatString: String): List<String> {
        val formatSpecifiers = FORMAT_SPECIFIER_REGEX.findAll(formatString)
        return formatSpecifiers.map { determineDataType(it.value) }.toList()
    }

    private fun determineDataType(formatSpecifier: String): String {
        return when {
            formatSpecifier.contains("\$s") -> "String"
            formatSpecifier.contains("\$d") -> "Int"
            formatSpecifier.contains("\$f") -> "Float"
            formatSpecifier.contains("s") -> "String"
            formatSpecifier.contains("d") -> "Int"
            formatSpecifier.contains("f") -> "Float"
            else -> "Any"
        }
    }
}
