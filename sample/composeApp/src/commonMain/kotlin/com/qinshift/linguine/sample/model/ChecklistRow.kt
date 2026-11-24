package com.qinshift.linguine.sample.model

internal data class ChecklistRow(
    val id: String,
    val rowId: ChecklistRowId,
    val count: Number?,
    val pluralCategory: PluralForm?,
    val template: String,
    val expected: String,
    val actual: String,
    val simulatedFallback: String? = null,
    val status: ChecklistStatus,
) {

    constructor(
        sectionId: ChecklistSectionId,
        rowId: ChecklistRowId,
        expected: String,
        template: String,
        actual: String,
        actualFailed: Boolean,
        simulatedFallback: String? = null,
        count: Number? = null,
        pluralCategory: PluralForm? = null,
    ): this(
        id = "${sectionId.key}-${rowId.key}",
        rowId = rowId,
        count = count,
        pluralCategory = pluralCategory,
        template = template,
        expected = expected,
        actual = actual,
        simulatedFallback = simulatedFallback,
        status = if (!actualFailed && expected == actual) ChecklistStatus.Passed else ChecklistStatus.Failed,
    )
}
