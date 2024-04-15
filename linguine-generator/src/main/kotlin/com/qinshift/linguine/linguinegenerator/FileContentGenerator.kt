package com.qinshift.linguine.linguinegenerator

class FileContentGenerator(private val fileContent: Map<String, String>) {
    fun generateFileContent(root: MutableMap<String, Any>): StringBuilder {
        val stringBuilder = StringBuilder("public object Strings {\n")
        generateKotlinCode(stringBuilder, root, 1)
        stringBuilder.append("}\n")
        return stringBuilder
    }

    private fun generateKotlinCode(builder: StringBuilder, map: Map<String, Any>, depth: Int) {
        map.forEach { (key, value) ->
            if (value is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                appendKotlinObject(builder, key, value as Map<String, Any>, depth)
            } else {
                appendFunctionOrValue(builder, key, value.toString(), depth)
            }
        }
    }

    private fun appendKotlinObject(
        builder: StringBuilder,
        key: String,
        value: Map<String, Any>,
        depth: Int,
    ) {
        val indent = "\t".repeat(depth)
        builder.append("$indent public object $key {\n")
        generateKotlinCode(builder, value, depth + 1)
        builder.append("$indent}\n")
    }

    private fun appendFunctionOrValue(
        builder: StringBuilder,
        key: String,
        value: String,
        depth: Int,
    ) {
        val indent = "\t".repeat(depth)
        val translation = fileContent.filter { it.key == value }.toString()
        val dataTypes = determineDataTypes(translation)

        if (dataTypes.isNotEmpty()) {
            appendFunctionDeclaration(builder, key, value, dataTypes, indent)
        } else {
            builder.append("$indent public val $key: String = localise(\"$value\")\n")
        }
    }

    @Suppress("MaximumLineLength", "MaxLineLength")
    private fun appendFunctionDeclaration(
        builder: StringBuilder,
        key: String,
        value: String,
        dataTypes: List<String>,
        indent: String,
    ) {
        var funcString = "$indent public fun $key("
        dataTypes.forEachIndexed { index, type ->
            funcString += if (index > 0) ", " else ""
            funcString += "param$index: $type"
        }
        funcString += "): String {\n$indent\t return localise(\"$value\", ${dataTypes.indices.joinToString { "param$it" }})\n$indent }\n"
        builder.append(funcString)
    }

    // %s - valid parameter, can be without $
    private fun determineDataTypes(formatString: String): List<String> {
        val formatSpecifiers = Regex("%[0-9]*\\\$[sdf]|%[sdf]").findAll(formatString)
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
