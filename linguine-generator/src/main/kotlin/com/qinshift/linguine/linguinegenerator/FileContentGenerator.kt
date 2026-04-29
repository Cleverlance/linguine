package com.qinshift.linguine.linguinegenerator

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.File
import java.nio.file.Path
import kotlin.reflect.KClass

public class FileContentGenerator(
    private val sourceRoot: Path,
    private val outputDirectory: Path,
    private val outputSuffix: String,
) {
    public fun generateFileContents(groupedMap: Map<String, Node.Group>): Map<Path, String> {
        return groupedMap.map { (fileName, content) ->
            val rootObjectName = "${fileName.toTypeName()}$outputSuffix"
            val filePath = outputDirectory.resolve("$rootObjectName.kt")

            filePath to generateFileContent(filePath, rootObjectName, content)
        }.toMap()
    }

    public fun generateFileContent(filePath: Path, fileName: String, root: Node.Group): String {
        val builder = FileSpec.builder(getFilePackage(filePath), fileName)
            .indent(DEFAULT_INDENT)
            .addImport(
                "com.qinshift.linguine.linguineruntime.presentation",
                "Localiser.localise",
            )

        if (hasPluralEntries(root)) {
            builder.addImport(
                "com.qinshift.linguine.linguineruntime.presentation",
                "Localiser.localisePlural",
            )
        }

        return builder
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

    private fun TypeSpec.Builder.addObjectContent(root: Node.Group): TypeSpec.Builder {
        root.children.forEach { (key, value) ->
            when (value) {
                is Node.Group -> {
                    val builder = TypeSpec.objectBuilder(key.toTypeName())
                    val type = builder
                        .addObjectContent(value)
                        .build()
                    addType(type)
                }

                is Node.Item -> {
                    addFunctionOrProperty(key, value.key, value.translation)
                }
            }
        }
        return this
    }

    private fun TypeSpec.Builder.addFunctionOrProperty(key: String, originalKey: String, translation: Translation) {
        when (translation) {
            is Translation.Text -> {
                if (hasFormatArguments(translation.value)) {
                    addTextFunction(
                        key = key,
                        originalKey = originalKey,
                        formatString = translation.value,
                    )
                } else {
                    addTextProperty(key = key, originalKey = originalKey)
                }
            }

            is Translation.Plural -> {
                addPluralFunction(
                    key = key,
                    originalKey = originalKey,
                    formatStrings = translation.forms.values,
                )
            }
        }
    }

    private fun TypeSpec.Builder.addTextFunction(key: String, originalKey: String, formatString: String) {
        val parameters = buildFormatParameters(formatString)
        val spec = FunSpec.builder(key)
            .apply { parameters.forEach(::addParameter) }
            .returns(String::class)
            .addStatement("""return localise(%L)""", buildFunctionArguments(originalKey, parameters))
            .build()
        addFunction(spec)
    }

    private fun TypeSpec.Builder.addTextProperty(key: String, originalKey: String) {
        val spec = PropertySpec.builder(key, String::class)
            .initializer("""localise("%L")""", originalKey)
            .build()
        addProperty(spec)
    }

    private fun TypeSpec.Builder.addPluralFunction(
        key: String,
        originalKey: String,
        formatStrings: Collection<String>,
    ) {
        val parameters = buildFormatParameters(formatStrings)
        val spec = FunSpec.builder(key)
            .addParameter("count", Number::class)
            .apply { parameters.forEach(::addParameter) }
            .returns(String::class)
            .addStatement(
                """return localisePlural(%L)""",
                buildFunctionArguments(originalKey, parameters, leadingArguments = listOf("count")),
            )
            .build()
        addFunction(spec)
    }

    private fun buildFormatParameters(formatString: String): List<ParameterSpec> {
        return buildFormatParameters(parseFormatSpecifiers(formatString))
    }

    private fun buildFormatParameters(formatStrings: Collection<String>): List<ParameterSpec> {
        return buildFormatParameters(formatStrings.flatMap(::parseFormatSpecifiers))
    }

    private fun parseFormatSpecifiers(formatString: String): List<FormatSpecifier> {
        val matches = FORMAT_SPECIFIER_REGEX.findAll(formatString).toList()
        validateFormatSpecifiers(formatString, matches)
        var nextSequentialIndex = 1

        return matches.map { match ->
            val explicitIndex = match.value
                .drop(1)
                .substringBefore('$', "")
                .takeIf(String::isNotEmpty)
                ?.toInt()
            val parameterIndex = explicitIndex ?: nextSequentialIndex++
            val specifier = match.value.lastOrNull()
                ?: error("Expected format specifier in '${match.value}'.")

            FormatSpecifier(parameterIndex, specifier)
        }
    }

    private fun buildFormatParameters(formatSpecifiers: List<FormatSpecifier>): List<ParameterSpec> {
        validateSequentialParameterIndexes(formatSpecifiers.map(FormatSpecifier::parameterIndex))

        val indexedSpecifiers = linkedMapOf<Int, Char>()
        formatSpecifiers.forEach { formatSpecifier ->
            indexedSpecifiers[formatSpecifier.parameterIndex] = formatSpecifier.specifier
        }

        return indexedSpecifiers.entries
            .sortedBy { it.key }
            .map { (index, specifier) ->
                ParameterSpec
                    .builder("param$index", determineParameterType(specifier))
                    .build()
            }
    }

    private fun validateFormatSpecifiers(
        formatString: String,
        matches: List<MatchResult>,
    ) {
        val malformedSpecifiers = MALFORMED_FORMAT_SPECIFIER_REGEX.findAll(formatString)
            .map(MatchResult::value)
            .toList()
        require(malformedSpecifiers.isEmpty()) {
            """
                Expected format specifiers to be either unindexed or indexed with a number. 
                Malformed: ${malformedSpecifiers.joinToString(", ")}.
            """.trimIndent()
        }

        val hasUnindexedSpecifier = matches.any { '$' !in it.value }
        require(matches.size <= 1 || !hasUnindexedSpecifier) {
            """
                Expected all format specifiers to be indexed 
                when a string contains multiple placeholders: '$formatString'
            """.trimIndent()
        }
    }

    private fun validateSequentialParameterIndexes(parameterIndexes: List<Int>) {
        val indexes = parameterIndexes.distinct().sorted()
        if (indexes.isEmpty()) return

        val expectedIndexes = (1..indexes.last()).toList()
        val missingIndexes = expectedIndexes - indexes.toSet()
        require(missingIndexes.isEmpty()) {
            """
                Expected format specifier indexes to be sequential from 1 to ${indexes.last()}. 
                Missing: ${missingIndexes.joinToString(", ")}.
            """.trimIndent()
        }
    }

    private fun determineParameterType(specifier: Char): KClass<*> {
        return when (specifier.lowercaseChar()) {
            's', 'd', 'f' -> String::class
            else -> error("Unsupported format specifier '%$specifier'.")
        }
    }

    private fun buildFunctionArguments(
        originalKey: String,
        parameters: List<ParameterSpec>,
        leadingArguments: List<String> = emptyList(),
    ): String {
        val arguments = listOf("\"$originalKey\"") + leadingArguments + parameters.map(ParameterSpec::name)
        return arguments.joinToString(", ")
    }

    private fun hasFormatArguments(formatString: String): Boolean {
        return FORMAT_SPECIFIER_REGEX.containsMatchIn(formatString)
    }

    private fun hasPluralEntries(root: Node.Group): Boolean {
        return root.children.values.any { value ->
            when (value) {
                is Node.Group -> hasPluralEntries(value)
                is Node.Item -> value.translation is Translation.Plural
            }
        }
    }

    private fun String.toTypeName(): String {
        return replaceFirstChar(Char::titlecase)
    }

    private companion object {
        const val DEFAULT_INDENT = "    "
        val FORMAT_SPECIFIER_REGEX = Regex("%[0-9]*\\$?[sdf]")
        val MALFORMED_FORMAT_SPECIFIER_REGEX = Regex("%\\$[sdf]")
    }

    private data class FormatSpecifier(
        val parameterIndex: Int,
        val specifier: Char,
    )
}
