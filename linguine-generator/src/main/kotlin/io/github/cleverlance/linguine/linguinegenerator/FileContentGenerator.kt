package io.github.cleverlance.linguine.linguinegenerator

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.name
import kotlin.io.path.relativeTo
import kotlin.reflect.KClass

class FileContentGenerator(
    private val filePath: Path,
    private val fileContent: Map<String, String>,
) {

    private companion object {
        const val DEFAULT_INDENT = "    "
        val FORMAT_SPECIFIER_REGEX = Regex("%[0-9]*\\\$[sdf]|%[sdf]")
    }

    private val filePackage: String by lazy {
        fun Path.isSourceDirectory(): Boolean = isDirectory()
            && (name == "kotlin" || name == "java")
            && parent?.parent?.name == "src"

        var sourcePath: Path? = filePath
        while (sourcePath != null && !sourcePath.isSourceDirectory()) {
            sourcePath = sourcePath.parent
        }

        if (sourcePath == null) return@lazy "" // no package

        val relativeDirectoryPath = filePath.parent.relativeTo(sourcePath)
        relativeDirectoryPath.joinToString(separator = ".") { path -> path.name }
    }

    fun generateFileContent(root: Map<String, Any>): String {
        return FileSpec.builder(filePackage, "")
            .indent(DEFAULT_INDENT)
            .addImport(
                "io.github.cleverlance.linguine.linguineruntime.presentation",
                "Localiser.localise",
            )
            .addType(
                TypeSpec.objectBuilder("Strings")
                    .addObjectContent(root)
                    .build(),
            )
            .build()
            .toString()
    }

    private fun TypeSpec.Builder.addObjectContent(root: Map<String, Any>): TypeSpec.Builder {
        @Suppress("UNCHECKED_CAST") // presuming the structure of the map
        root.forEach { (key, value) ->
            when (value) {
                is Map<*, *> -> addObject(key, value as Map<String, Any>)
                else -> addFunctionOrProperty(key, value.toString())
            }
        }
        return this
    }

    private fun TypeSpec.Builder.addObject(key: String, value: Map<String, Any>) {
        addType(
            TypeSpec.objectBuilder(key)
                .addObjectContent(value)
                .build(),
        )
    }

    private fun TypeSpec.Builder.addFunctionOrProperty(key: String, value: String) {
        val translation = fileContent.filter { it.key == value }.toString()
        val dataTypes = determineDataTypes(translation)

        if (dataTypes.isNotEmpty()) {
            val parameters = dataTypes.mapIndexed { index, type ->
                ParameterSpec.builder("param${index + 1}", type)
                    .build()
            }
            val parametersCall = parameters.joinToString { it.name }
            addFunction(
                FunSpec.builder(key)
                    .addParameters(parameters)
                    .returns(String::class)
                    .addStatement("""return localise("$value", $parametersCall)""")
                    .build(),
            )
        } else {
            addProperty(
                PropertySpec.builder(key, String::class)
                    .initializer("""localise("$value")""")
                    .build(),
            )
        }
    }

    // %s - valid parameter, can be without $
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
}
