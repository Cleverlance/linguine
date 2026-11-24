package com.qinshift.linguine.sample.model

internal data class ChecklistSection(
    val id: ChecklistSectionId,
    val rows: List<ChecklistRow>,
)
