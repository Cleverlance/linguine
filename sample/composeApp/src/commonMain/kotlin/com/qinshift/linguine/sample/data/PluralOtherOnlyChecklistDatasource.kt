package com.qinshift.linguine.sample.data

import com.qinshift.linguine.sample.model.ChecklistCallResult
import com.qinshift.linguine.sample.model.ChecklistExpectedPolicy
import com.qinshift.linguine.sample.model.ChecklistInputs
import com.qinshift.linguine.sample.model.ChecklistRow
import com.qinshift.linguine.sample.model.ChecklistRowId
import com.qinshift.linguine.sample.model.ChecklistSectionId
import com.qinshift.linguine.sample.model.Language
import com.qinshift.linguine.sample.model.PluralForm
import com.qinshift.linguine.sample.localization.requireotheronly.generated.SampleStrings as OtherOnlyStrings

internal class PluralOtherOnlyChecklistDatasource(
    private val checklistExpectedDatasource: ChecklistExpectedDatasource,
    private val languagePluralRulesDatasource: LanguagePluralRulesDatasource,
) : ChecklistDatasource {
    override val id: ChecklistSectionId = ChecklistSectionId.OtherOnly
    private val allPluralForms = PluralForm.entries.toSet()

    override fun rows(
        inputs: ChecklistInputs,
        enabledPluralForms: Set<PluralForm>,
    ): List<ChecklistRow> {
        val categories = languagePluralRulesDatasource.selectCategories(inputs.language, inputs.count)
        val fallbackCategories = languagePluralRulesDatasource.selectCategories(Language.English, inputs.count)
        val category = categories.first()
        val fallbackCategory = fallbackCategories.first()
        val countText = inputs.count.toString()

        return listOf(
            row(
                inputs = inputs,
                rowId = ChecklistRowId.Plural.OtherOnlyItemCount,
                expected = pluralValue(
                    language = inputs.language,
                    rowId = ChecklistRowId.Plural.OtherOnlyItemCount,
                    count = inputs.count,
                    enabledPluralForms = allPluralForms,
                    inputs.param1,
                ),
                template = pluralTemplate(
                    language = inputs.language,
                    rowId = ChecklistRowId.Plural.OtherOnlyItemCount,
                    count = inputs.count,
                    enabledPluralForms = allPluralForms,
                ),
                originalActual = resolve {
                    OtherOnlyStrings.Plural.itemCount(inputs.count, inputs.param1)
                },
                count = inputs.count,
                pluralCategory = category,
                pluralCategories = categories,
                enabledPluralForms = enabledPluralForms,
                effectiveActualArgs = listOf(inputs.param1),
            ),
            row(
                inputs = inputs,
                rowId = ChecklistRowId.Plural.OtherOnlyReversedPlaceholders,
                expected = pluralValue(
                    language = inputs.language,
                    rowId = ChecklistRowId.Plural.OtherOnlyReversedPlaceholders,
                    count = inputs.count,
                    enabledPluralForms = allPluralForms,
                    inputs.param1,
                    inputs.param2,
                    inputs.param3,
                ),
                template = pluralTemplate(
                    language = inputs.language,
                    rowId = ChecklistRowId.Plural.OtherOnlyReversedPlaceholders,
                    count = inputs.count,
                    enabledPluralForms = allPluralForms,
                ),
                originalActual = resolve {
                    OtherOnlyStrings.Plural.reversedPlaceholders(
                        count = inputs.count,
                        param1 = inputs.param1,
                        param2 = inputs.param2,
                        param3 = inputs.param3,
                    )
                },
                count = inputs.count,
                pluralCategory = category,
                pluralCategories = categories,
                enabledPluralForms = enabledPluralForms,
                effectiveActualArgs = listOf(inputs.param1, inputs.param2, inputs.param3),
            ),
            row(
                inputs = inputs,
                rowId = ChecklistRowId.Plural.OtherOnlyCategoryName,
                expected = pluralValue(
                    language = inputs.language,
                    rowId = ChecklistRowId.Plural.OtherOnlyCategoryName,
                    count = inputs.count,
                    enabledPluralForms = allPluralForms,
                    countText,
                    inputs.param1,
                    inputs.param2,
                    inputs.param3,
                ),
                template = pluralTemplate(
                    language = inputs.language,
                    rowId = ChecklistRowId.Plural.OtherOnlyCategoryName,
                    count = inputs.count,
                    enabledPluralForms = allPluralForms,
                ),
                originalActual = resolve {
                    OtherOnlyStrings.Plural.categoryName(
                        count = inputs.count,
                        param1 = countText,
                        param2 = inputs.param1,
                        param3 = inputs.param2,
                        param4 = inputs.param3,
                    )
                },
                count = inputs.count,
                pluralCategory = category,
                pluralCategories = categories,
                enabledPluralForms = enabledPluralForms,
                effectiveActualArgs = listOf(countText, inputs.param1, inputs.param2, inputs.param3),
            ),
            row(
                inputs = inputs,
                rowId = ChecklistRowId.Plural.OtherOnlyFallbackCount,
                expected = pluralValue(
                    language = inputs.language,
                    rowId = ChecklistRowId.Plural.OtherOnlyFallbackCount,
                    count = inputs.count,
                    enabledPluralForms = allPluralForms,
                    countText,
                    inputs.param1,
                    inputs.param2,
                    inputs.param3,
                ),
                template = pluralTemplate(
                    language = inputs.language,
                    rowId = ChecklistRowId.Plural.OtherOnlyFallbackCount,
                    count = inputs.count,
                    enabledPluralForms = allPluralForms,
                ),
                originalActual = resolve {
                    OtherOnlyStrings.Plural.fallbackCount(
                        count = inputs.count,
                        param1 = countText,
                        param2 = inputs.param1,
                        param3 = inputs.param2,
                        param4 = inputs.param3,
                    )
                },
                count = inputs.count,
                pluralCategory = fallbackCategory,
                pluralCategories = fallbackCategories,
                enabledPluralForms = enabledPluralForms,
                effectiveActualArgs = listOf(countText, inputs.param1, inputs.param2, inputs.param3),
            ),
        )
    }

    private fun row(
        inputs: ChecklistInputs,
        rowId: ChecklistRowId.Plural,
        expected: String,
        template: String,
        originalActual: ChecklistCallResult,
        enabledPluralForms: Set<PluralForm>,
        effectiveActualArgs: List<String>,
        count: Number? = null,
        pluralCategory: PluralForm? = null,
        pluralCategories: List<PluralForm> = emptyList(),
    ): ChecklistRow {
        val effectivePluralCategory = effectiveCategory(
            categories = pluralCategories.ifEmpty {
                pluralCategory?.let(::listOf).orEmpty()
            },
            enabledPluralForms = enabledPluralForms,
        )
        val simulatedFallback = if (
            originalActual is ChecklistCallResult.Success &&
            pluralCategory != null &&
            effectivePluralCategory != pluralCategory
        ) {
            checklistExpectedDatasource.format(
                pluralTemplate(
                    language = inputs.language,
                    rowId = rowId,
                    count = count ?: inputs.count,
                    enabledPluralForms = setOf(effectivePluralCategory),
                ),
                *effectiveActualArgs.toTypedArray(),
            )
        } else {
            null
        }

        return ChecklistRow(
            sectionId = id,
            rowId = rowId,
            expected = expected,
            template = template,
            actual = originalActual.text,
            actualFailed = originalActual is ChecklistCallResult.Error,
            simulatedFallback = simulatedFallback,
            count = count,
            pluralCategory = pluralCategory,
        )
    }

    private fun pluralValue(
        language: Language,
        rowId: ChecklistRowId.Plural,
        count: Number,
        enabledPluralForms: Set<PluralForm>,
        vararg args: String,
    ): String = checklistExpectedDatasource.format(pluralTemplate(language, rowId, count, enabledPluralForms), *args)

    private fun pluralTemplate(
        language: Language,
        rowId: ChecklistRowId.Plural,
        count: Number,
        enabledPluralForms: Set<PluralForm>,
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
            policy = ChecklistExpectedPolicy.OtherOnly,
            rowId = rowId,
            language = targetLanguage,
            form = effectiveCategory(
                categories = languagePluralRulesDatasource.selectCategories(targetLanguage, count),
                enabledPluralForms = enabledPluralForms,
            ),
        )
    }

    private fun effectiveCategory(
        categories: List<PluralForm>,
        enabledPluralForms: Set<PluralForm>,
    ): PluralForm {
        return categories.firstOrNull { form ->
            form in enabledPluralForms
        } ?: PluralForm.mandatory
    }
}
