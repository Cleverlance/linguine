package com.qinshift.linguine.sample.data

import com.qinshift.linguine.sample.model.ChecklistCallResult
import com.qinshift.linguine.sample.model.ChecklistExpectedPolicy
import com.qinshift.linguine.sample.model.ChecklistInputs
import com.qinshift.linguine.sample.model.ChecklistRow
import com.qinshift.linguine.sample.model.ChecklistRowId
import com.qinshift.linguine.sample.model.ChecklistSectionId
import com.qinshift.linguine.sample.model.Language
import com.qinshift.linguine.sample.model.PluralForm
import com.qinshift.linguine.sample.localization.requireallforms.generated.SampleStrings as AllFormsStrings

internal class PluralAllFormsChecklistDatasource(
    private val checklistExpectedDatasource: ChecklistExpectedDatasource,
    private val languagePluralRulesDatasource: LanguagePluralRulesDatasource,
) : ChecklistDatasource {
    override val id: ChecklistSectionId = ChecklistSectionId.AllForms

    override fun rows(
        inputs: ChecklistInputs,
        enabledPluralForms: Set<PluralForm>,
    ): List<ChecklistRow> {
        val category = languagePluralRulesDatasource.selectCategory(inputs.language, inputs.count)
        val fallbackCategory = languagePluralRulesDatasource.selectCategory(Language.English, inputs.count)
        val countText = inputs.count.toString()

        return listOf(
            row(
                rowId = ChecklistRowId.Plural.AllFormsItemCount,
                expected = pluralValue(
                    language = inputs.language,
                    rowId = ChecklistRowId.Plural.AllFormsItemCount,
                    count = inputs.count,
                    inputs.param1,
                    inputs.param2,
                    inputs.param3,
                ),
                template = pluralTemplate(inputs.language, ChecklistRowId.Plural.AllFormsItemCount, inputs.count),
                actual = resolve {
                    AllFormsStrings.Plural.itemCount(
                        count = inputs.count,
                        param1 = inputs.param1,
                        param2 = inputs.param2,
                        param3 = inputs.param3,
                    )
                },
                count = inputs.count,
                pluralCategory = category,
            ),
            row(
                rowId = ChecklistRowId.Plural.AllFormsSparsePlaceholders,
                expected = pluralValue(
                    language = inputs.language,
                    rowId = ChecklistRowId.Plural.AllFormsSparsePlaceholders,
                    count = inputs.count,
                    inputs.param1,
                    inputs.param2,
                    inputs.param3,
                ),
                template = pluralTemplate(inputs.language, ChecklistRowId.Plural.AllFormsSparsePlaceholders, inputs.count),
                actual = resolve {
                    AllFormsStrings.Plural.sparsePlaceholders(
                        count = inputs.count,
                        param1 = inputs.param1,
                        param2 = inputs.param2,
                        param3 = inputs.param3,
                    )
                },
                count = inputs.count,
                pluralCategory = category,
            ),
            row(
                rowId = ChecklistRowId.Plural.AllFormsCategoryName,
                expected = pluralValue(
                    language = inputs.language,
                    rowId = ChecklistRowId.Plural.AllFormsCategoryName,
                    count = inputs.count,
                    countText,
                    inputs.param1,
                    inputs.param2,
                    inputs.param3,
                ),
                template = pluralTemplate(inputs.language, ChecklistRowId.Plural.AllFormsCategoryName, inputs.count),
                actual = resolve {
                    AllFormsStrings.Plural.categoryName(
                        count = inputs.count,
                        param1 = countText,
                        param2 = inputs.param1,
                        param3 = inputs.param2,
                        param4 = inputs.param3,
                    )
                },
                count = inputs.count,
                pluralCategory = category,
            ),
            row(
                rowId = ChecklistRowId.Plural.AllFormsFallbackCount,
                expected = pluralValue(
                    language = inputs.language,
                    rowId = ChecklistRowId.Plural.AllFormsFallbackCount,
                    count = inputs.count,
                    countText,
                    inputs.param1,
                    inputs.param2,
                    inputs.param3,
                ),
                template = pluralTemplate(inputs.language, ChecklistRowId.Plural.AllFormsFallbackCount, inputs.count),
                actual = resolve {
                    AllFormsStrings.Plural.fallbackCount(
                        count = inputs.count,
                        param1 = countText,
                        param2 = inputs.param1,
                        param3 = inputs.param2,
                        param4 = inputs.param3,
                    )
                },
                count = inputs.count,
                pluralCategory = fallbackCategory,
            ),
        )
    }

    private fun row(
        rowId: ChecklistRowId.Plural,
        expected: String,
        template: String,
        actual: ChecklistCallResult,
        count: Number? = null,
        pluralCategory: PluralForm? = null,
    ): ChecklistRow = ChecklistRow(
        sectionId = id,
        rowId = rowId,
        expected = expected,
        template = template,
        actual = actual.text,
        actualFailed = actual is ChecklistCallResult.Error,
        count = count,
        pluralCategory = pluralCategory,
    )

    private fun pluralValue(
        language: Language,
        rowId: ChecklistRowId.Plural,
        count: Number,
        vararg args: String,
    ): String = checklistExpectedDatasource.format(pluralTemplate(language, rowId, count), *args)

    private fun pluralTemplate(
        language: Language,
        rowId: ChecklistRowId.Plural,
        count: Number,
    ): String {
        if (languagePluralRulesDatasource.hasUnsupportedPluralRules(language) && rowId.usesEnglishFallback.not()) {
            return languagePluralRulesDatasource.unsupportedPluralRulesMessage(language)
        }

        val targetLanguage = if (rowId.usesEnglishFallback) {
            Language.English
        } else {
            language
        }

        return checklistExpectedDatasource.pluralTemplate(
            policy = ChecklistExpectedPolicy.AllForms,
            rowId = rowId,
            language = targetLanguage,
            form = languagePluralRulesDatasource.selectCategory(targetLanguage, count),
        )
    }
}
