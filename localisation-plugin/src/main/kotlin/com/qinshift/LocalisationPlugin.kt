package com.qinshift

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class LocalisationPlugin : Plugin<Project> {

    private var jsonContent: Map<String, String> = emptyMap()

    override fun apply(project: Project) {

        val extension = project.extensions.create("localisation", LocalisationExtension::class.java)

        project.task("loc") {
            doLast {

                // Read JSON File
                readJSON(project, extension)

                // Generate content for the Kotlin Localization File
                val outputFileContent = generateFileContent(extension)

                // Write built kotlin class and its nested structure into Kotlin File
                File("${extension.outputDirPath}/${extension.stringsFileName}").apply {
                    parentFile.mkdirs()
                    writeText(outputFileContent.toString())
                }
            }
        }
    }

    private fun readJSON(project: Project, extension: LocalisationExtension) {
        val jsonFile = File(project.projectDir, extension.jsonFilePath)
        val type = object : TypeToken<Map<String, String>>() {}.type
        val json: Map<String, String> = Gson().fromJson(jsonFile.readText(), type)
        jsonContent = json
    }

    private fun generateFileContent(extension: LocalisationExtension): StringBuilder {
        val root = generateNestedMapStructureFromJSON(extension)
        val stringBuilder = StringBuilder("public object Strings {\n")
        generateKotlinCode(stringBuilder, root, 1)
        stringBuilder.append("}\n")
        return stringBuilder
    }

    private fun generateNestedMapStructureFromJSON(extension: LocalisationExtension): MutableMap<String, Any> {
        val root = mutableMapOf<String, Any>()
        jsonContent.keys.forEach { key ->
            val parts = transformKeyToCamelCaseSegments(key, extension)
            updateNestedMapStructure(root, parts, key)
        }
        return root
    }


    private fun transformKeyToCamelCaseSegments(key: String, extension: LocalisationExtension): List<String> {
        return key.split(extension.majorDelimiter).map { hierarchicalSegment ->
            hierarchicalSegment.split(extension.minorDelimiter).joinToString("") { word ->
                word.replaceFirstChar { it.uppercaseChar() }
            }
        }
    }

    private fun updateNestedMapStructure(
        root: MutableMap<String, Any>,
        parts: List<String>,
        fullKey: String
    ) {
        var current = root
        parts.forEachIndexed { index, part ->
            val formattedPart = formatPart(part, index, parts)
            if (index == parts.lastIndex) {
                current[formattedPart] = fullKey
            } else {
                current =
                    current.computeIfAbsent(formattedPart) { mutableMapOf<String, Any>() } as MutableMap<String, Any>
            }
        }
    }

    private fun formatPart(part: String, index: Int, parts: List<String>): String {
        return if (index < parts.lastIndex) {
            part.replaceFirstChar { it.uppercaseChar() }
        } else {
            part.replaceFirstChar { it.lowercase() }
        }
    }

    private fun generateKotlinCode(builder: StringBuilder, map: Map<String, Any>, depth: Int) {
        map.forEach { (key, value) ->
            if (value is Map<*, *>) {
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
        depth: Int
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
        depth: Int
    ) {
        val indent = "\t".repeat(depth)
        val translation = jsonContent.filter { it.key == value }.toString()
        val dataTypes = determineDataTypes(translation)

        if (dataTypes.isNotEmpty()) {
            appendFunctionDeclaration(builder, key, value, dataTypes, indent)
        } else {
            builder.append("$indent public val $key: String = localise(\"$value\")\n")
        }
    }

    private fun appendFunctionDeclaration(
        builder: StringBuilder,
        key: String,
        value: String,
        dataTypes: List<String>,
        indent: String
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