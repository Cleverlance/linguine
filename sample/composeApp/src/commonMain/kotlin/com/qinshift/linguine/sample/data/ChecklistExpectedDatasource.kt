package com.qinshift.linguine.sample.data

import com.qinshift.linguine.sample.model.ChecklistExpectedPolicy
import com.qinshift.linguine.sample.model.ChecklistRowId
import com.qinshift.linguine.sample.model.Language
import com.qinshift.linguine.sample.model.PluralForm
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal class ChecklistExpectedDatasource {

    private val root = Json.parseToJsonElement(CHECKLIST_EXPECTED_JSON).jsonObject

    fun plainTemplate(
        rowId: ChecklistRowId.Plain,
        language: Language,
    ): String {
        return root
            .objectAt(ChecklistFixtureSection.Plain.key)
            .objectAt(rowId.expectedKey)
            .stringAt(language.code)
    }

    fun pluralTemplate(
        policy: ChecklistExpectedPolicy,
        rowId: ChecklistRowId.Plural,
        language: Language,
        form: PluralForm,
    ): String {
        val forms = root
            .objectAt(ChecklistFixtureSection.Plural.key)
            .objectAt(policy.key)
            .objectAt(rowId.expectedKey)
            .objectAt(language.code)

        return forms[form.key]?.jsonPrimitive?.content
            ?: forms.stringAt(PluralForm.mandatory.key)
    }

    fun format(
        template: String,
        vararg args: String,
    ): String {
        var nextUnindexedArgument = 0
        return FORMAT_SPECIFIER_REGEX.replace(template) { match ->
            val explicitIndex = match.groupValues[1]
                .takeIf(String::isNotEmpty)
                ?.toInt()
            val argumentIndex = explicitIndex?.dec() ?: nextUnindexedArgument++

            args.getOrNull(argumentIndex) ?: match.value
        }
    }

    private fun JsonObject.objectAt(key: String): JsonObject {
        return this[key]?.jsonObject
            ?: error("Expected checklist fixture object '$key'.")
    }

    private fun JsonObject.stringAt(key: String): String {
        return this[key]?.jsonPrimitive?.content
            ?: error("Expected checklist fixture string '$key'.")
    }

    private companion object {
        private val FORMAT_SPECIFIER_REGEX = Regex("%(?:([0-9]+)\\$)?[sdf]")
    }

    private enum class ChecklistFixtureSection(val key: String) {
        Plain(key = "plain"),
        Plural(key = "plural"),
    }
}
