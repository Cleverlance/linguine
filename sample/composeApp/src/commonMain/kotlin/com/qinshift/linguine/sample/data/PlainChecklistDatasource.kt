package com.qinshift.linguine.sample.data

import com.qinshift.linguine.sample.localization.plain.generated.SampleStrings
import com.qinshift.linguine.sample.model.ChecklistCallResult
import com.qinshift.linguine.sample.model.ChecklistInputs
import com.qinshift.linguine.sample.model.ChecklistRow
import com.qinshift.linguine.sample.model.ChecklistRowId
import com.qinshift.linguine.sample.model.ChecklistSectionId
import com.qinshift.linguine.sample.model.Language
import com.qinshift.linguine.sample.model.PluralForm

internal class PlainChecklistDatasource(
    private val checklistExpectedDatasource: ChecklistExpectedDatasource,
) : ChecklistDatasource {
    override val id: ChecklistSectionId = ChecklistSectionId.Plain

    override fun rows(
        inputs: ChecklistInputs,
        enabledPluralForms: Set<PluralForm>,
    ): List<ChecklistRow> {
        return listOf(
            row(
                rowId = ChecklistRowId.Plain.Sentence,
                expected = plainValue(inputs.language, ChecklistRowId.Plain.Sentence),
                template = plainTemplate(inputs.language, ChecklistRowId.Plain.Sentence),
                actual = resolve { SampleStrings.Plain.sentence },
            ),
            row(
                rowId = ChecklistRowId.Plain.FallbackSentence,
                expected = plainValue(inputs.language, ChecklistRowId.Plain.FallbackSentence),
                template = plainTemplate(inputs.language, ChecklistRowId.Plain.FallbackSentence),
                actual = resolve { SampleStrings.Plain.fallbackSentence },
            ),
            row(
                rowId = ChecklistRowId.Plain.SinglePlaceholder,
                expected = plainValue(inputs.language, ChecklistRowId.Plain.SinglePlaceholder, inputs.param1),
                template = plainTemplate(inputs.language, ChecklistRowId.Plain.SinglePlaceholder),
                actual = resolve { SampleStrings.Plain.itemSentence(inputs.param1) },
            ),
            row(
                rowId = ChecklistRowId.Plain.OrderedPlaceholders,
                expected = plainValue(
                    language = inputs.language,
                    rowId = ChecklistRowId.Plain.OrderedPlaceholders,
                    inputs.param1,
                    inputs.param2,
                    inputs.param3,
                ),
                template = plainTemplate(inputs.language, ChecklistRowId.Plain.OrderedPlaceholders),
                actual = resolve {
                    SampleStrings.Plain.orderedSentence(
                        inputs.param1,
                        inputs.param2,
                        inputs.param3,
                    )
                },
            ),
            row(
                rowId = ChecklistRowId.Plain.ReversedPlaceholders,
                expected = plainValue(
                    language = inputs.language,
                    rowId = ChecklistRowId.Plain.ReversedPlaceholders,
                    inputs.param1,
                    inputs.param2,
                    inputs.param3,
                ),
                template = plainTemplate(inputs.language, ChecklistRowId.Plain.ReversedPlaceholders),
                actual = resolve {
                    SampleStrings.Plain.reversedSentence(
                        inputs.param1,
                        inputs.param2,
                        inputs.param3,
                    )
                },
            ),
            row(
                rowId = ChecklistRowId.Plain.FallbackPlaceholders,
                expected = plainValue(
                    inputs.language,
                    ChecklistRowId.Plain.FallbackPlaceholders,
                    inputs.param1,
                    inputs.param2,
                    inputs.param3,
                ),
                template = plainTemplate(inputs.language, ChecklistRowId.Plain.FallbackPlaceholders),
                actual = resolve {
                    SampleStrings.Plain.fallbackPlaceholders(
                        inputs.param1,
                        inputs.param2,
                        inputs.param3,
                    )
                },
            ),
        )
    }

    private fun row(
        rowId: ChecklistRowId.Plain,
        expected: String,
        template: String,
        actual: ChecklistCallResult,
    ): ChecklistRow = ChecklistRow(
        sectionId = id,
        rowId = rowId,
        expected = expected,
        template = template,
        actual = actual.text,
        actualFailed = actual is ChecklistCallResult.Error,
    )

    private fun plainValue(
        language: Language,
        rowId: ChecklistRowId.Plain,
        vararg args: String,
    ): String = checklistExpectedDatasource.format(plainTemplate(language, rowId), *args)

    private fun plainTemplate(
        language: Language,
        rowId: ChecklistRowId.Plain,
    ): String {
        val targetLanguage = if (rowId.usesEnglishFallback) {
            Language.English
        } else {
            language
        }

        return checklistExpectedDatasource.plainTemplate(
            rowId = rowId,
            language = targetLanguage,
        )
    }
}
