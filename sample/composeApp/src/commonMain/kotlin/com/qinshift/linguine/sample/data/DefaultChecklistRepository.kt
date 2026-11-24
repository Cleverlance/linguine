package com.qinshift.linguine.sample.data

import com.qinshift.linguine.sample.domain.ChecklistRepository
import com.qinshift.linguine.sample.model.ChecklistInputs
import com.qinshift.linguine.sample.model.ChecklistSection
import com.qinshift.linguine.sample.model.ChecklistTab
import com.qinshift.linguine.sample.model.PluralForm
import com.qinshift.linguine.sample.model.ParamDefaultValues
import com.qinshift.linguine.sample.model.Language

internal class DefaultChecklistRepository(
    pluralAllFormsChecklistDatasource: PluralAllFormsChecklistDatasource,
    pluralOtherOnlyChecklistDatasource: PluralOtherOnlyChecklistDatasource,
    private val languagePluralRulesDatasource: LanguagePluralRulesDatasource,
    private val plainChecklistDatasource: PlainChecklistDatasource,
) : ChecklistRepository {

    private val pluralDatasources: List<ChecklistDatasource> = listOf(
        pluralAllFormsChecklistDatasource,
        pluralOtherOnlyChecklistDatasource,
    )

    override val languages: List<Language> = Language.entries.toList()

    override fun defaultParams(language: Language): ParamDefaultValues = when (language) {
        Language.Czech -> ParamDefaultValues(
            param1 = "jablka",
            param2 = "hrušky",
            param3 = "banány",
        )

        Language.English -> ParamDefaultValues(
            param1 = "apples",
            param2 = "pears",
            param3 = "bananas",
        )

        Language.French -> ParamDefaultValues(
            param1 = "pommes",
            param2 = "poires",
            param3 = "bananes",
        )

        Language.Slovak -> ParamDefaultValues(
            param1 = "jablká",
            param2 = "hrušky",
            param3 = "banány",
        )

        Language.Ukrainian -> ParamDefaultValues(
            param1 = "яблука",
            param2 = "груші",
            param3 = "банани",
        )

        Language.Spanish -> ParamDefaultValues(
            param1 = "manzanas",
            param2 = "peras",
            param3 = "plátanos",
        )
    }

    override fun subjectForm(
        language: Language,
        count: Number,
    ): String = languagePluralRulesDatasource.subjectForm(language, count)

    override fun sanitizeCountInput(value: String): String {
        var hasDecimalSeparator = false
        val result = StringBuilder()

        value.forEach { char ->
            when {
                char.isDigit() -> result.append(char)
                (char == '.' || char == ',') && !hasDecimalSeparator -> {
                    result.append('.')
                    hasDecimalSeparator = true
                }
                char == '-' && result.isEmpty() -> result.append(char)
            }
        }

        return result.toString()
    }

    override fun parseCount(value: String): Number {
        val normalizedValue = value.trim()
        return when {
            normalizedValue.isEmpty() || normalizedValue == "-" || normalizedValue == "." || normalizedValue == "-." -> 0
            "." in normalizedValue -> normalizedValue.toDoubleOrNull() ?: 0
            else -> normalizedValue.toIntOrNull() ?: normalizedValue.toDoubleOrNull() ?: 0
        }
    }

    override fun sections(
        inputs: ChecklistInputs,
        tab: ChecklistTab,
        enabledPluralForms: Set<PluralForm>,
    ): List<ChecklistSection> = when (tab) {
        ChecklistTab.Plain -> listOf(
            ChecklistSection(
                id = plainChecklistDatasource.id,
                rows = plainChecklistDatasource.rows(
                    inputs = inputs,
                    enabledPluralForms = enabledPluralForms,
                ),
            ),
        )

        ChecklistTab.Plural -> pluralDatasources.map { datasource ->
            ChecklistSection(
                id = datasource.id,
                rows = datasource.rows(
                    inputs = inputs,
                    enabledPluralForms = enabledPluralForms,
                ),
            )
        }
    }
}
