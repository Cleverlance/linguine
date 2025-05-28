package com.qinshift.linguine.linguinegenerator

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.File
import java.nio.file.Path
import java.util.Locale
import kotlin.reflect.KClass

class FileContentGenerator(
    private val sourceRoot: Path,
    private val outputDirectory: Path,
    private val fileContent: Map<String, String>,
) {

    fun generateFileContents(groupedMap: Map<String, Map<String, Any>>): Map<Path, String> {
        return groupedMap.map { (fileName, content) ->
            val capitalizedFileName = fileName.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
            val filePath = outputDirectory.resolve("${capitalizedFileName}Strings.kt")
            filePath to generateFileContent(filePath, capitalizedFileName, content)
        }.toMap()
    }

    fun generateFileContent(filePath: Path, fileName: String, root: Map<String, Any>): String {
        return FileSpec.builder(getFilePackage(filePath), fileName)
            .indent(DEFAULT_INDENT)
            .addImport(
                "com.qinshift.linguine.linguineruntime.presentation",
                "Localiser.localise",
            )
            .addType(
                TypeSpec.objectBuilder(fileName)
                    .addObjectContent(root)
                    .build(),
            )
            .build()
            .toString()
    }

    private fun getFilePackage(filePath: Path): String {
        val relativePath = sourceRoot.relativize(filePath.parent)
            .toString()
            .replace(File.separatorChar, '.')

        return relativePath.ifBlank { "presentation" }
    }

    private fun TypeSpec.Builder.addObjectContent(root: Map<String, Any>): TypeSpec.Builder {
        @Suppress("UNCHECKED_CAST")
        root.forEach { (key, value) ->
            when (value) {
                is Map<*, *> -> {
                    addType(
                        TypeSpec.objectBuilder(
                            key.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                            },
                        )
                            .addObjectContent(value as Map<String, Any>)
                            .build(),
                    )
                }

                is Pair<*, *> -> {
                    val originalKey = value.first as String
                    value.second as String
                    addFunctionOrProperty(key, originalKey)
                }
            }
        }
        return this
    }

    private fun TypeSpec.Builder.addFunctionOrProperty(key: String, originalKey: String) {
        val translation = fileContent[originalKey]
        val dataTypes = determineDataTypes(translation ?: "")

        if (dataTypes.isNotEmpty()) {
            val parameters = dataTypes.mapIndexed { index, type ->
                ParameterSpec.builder("param${index + 1}", type).build()
            }

            addFunction(
                FunSpec.builder(key)
                    .apply {
                        parameters.forEach { addParameter(it) }
                    }
                    .returns(String::class)
                    .addStatement(
                        """return localise("%L", ${parameters.joinToString(", ") { it.name }})""",
                        originalKey,
                    )
                    .build(),
            )
        } else {
            addProperty(
                PropertySpec.builder(key, String::class)
                    .initializer("""localise("%L")""", originalKey)
                    .build(),
            )
        }
    }

    private fun determineDataTypes(formatString: String): List<KClass<*>> {
        val formatSpecifiers = FORMAT_SPECIFIER_REGEX.findAll(formatString)
        return formatSpecifiers.map { determineDataType(it.value) }.toList()
    }

    private fun determineDataType(formatSpecifier: String): KClass<*> {
        return when {
            formatSpecifier.contains("\$s") -> String::class
            formatSpecifier.contains("\$d") -> Int::class
            formatSpecifier.contains("\$f") -> Float::class
            formatSpecifier.contains("s") -> String::class
            formatSpecifier.contains("d") -> Int::class
            formatSpecifier.contains("f") -> Float::class
            else -> Any::class
        }
    }

    private companion object {
        const val DEFAULT_INDENT = "    "
        val FORMAT_SPECIFIER_REGEX = Regex("%[0-9]*\\$?[sdf]")
    }
}
