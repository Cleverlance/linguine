package com.qinshift.linguine.linguinegenerator.filereader

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.qinshift.linguine.linguinegenerator.PluralFormPolicy
import com.qinshift.linguine.linguinegenerator.Translation
import java.io.File

public class FileReader(
    private val pluralFormPolicy: PluralFormPolicy,
) {
    public fun read(file: File, fileType: FileType): Map<String, Translation> {
        return when (fileType) {
            FileType.JSON -> parseJSON(file.readText())
        }
    }

    private fun parseJSON(fileContent: String): Map<String, Translation> {
        val root = JsonParser.parseString(fileContent)
        require(root.isJsonObject) { "Expected root JSON object for localisation file." }

        return root.asJsonObject.entrySet().associate { (key, value) ->
            key to parseValue(key, value)
        }
    }

    private fun parseValue(key: String, value: JsonElement): Translation {
        return when {
            value.isJsonPrimitive && value.asJsonPrimitive.isString -> {
                Translation.Text(value.asString)
            }

            value.isJsonObject -> {
                Translation.Plural(parsePluralForms(key, value.asJsonObject))
            }

            else -> {
                error("Expected string or object for key '$key' but was $value")
            }
        }
    }

    private fun parsePluralForms(key: String, value: JsonObject): Map<String, String> {
        val forms = value.entrySet().associate { (formKey, formValue) ->
            require(formValue.isJsonPrimitive && formValue.asJsonPrimitive.isString) {
                "Expected plural form '$formKey' of key '$key' to be a string."
            }
            formKey to formValue.asString
        }

        val missingForms = pluralFormPolicy.requiredForms.filterNot(forms::containsKey)
        require(missingForms.isEmpty()) {
            """
                Expected plural key '$key' to contain required forms: 
                ${pluralFormPolicy.requiredForms.joinToString(", ")}. 
                Missing: ${missingForms.joinToString(", ")}.
            """.trimIndent()
        }

        return forms
    }
}
