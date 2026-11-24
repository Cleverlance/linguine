package com.qinshift.linguine.sample.domain

import com.qinshift.linguine.sample.model.ChecklistInputs
import com.qinshift.linguine.sample.model.ChecklistSection
import com.qinshift.linguine.sample.model.ChecklistTab
import com.qinshift.linguine.sample.model.PluralForm
import com.qinshift.linguine.sample.model.ParamDefaultValues
import com.qinshift.linguine.sample.model.Language

internal interface ChecklistRepository {
    val languages: List<Language>

    fun defaultParams(language: Language): ParamDefaultValues

    fun subjectForm(
        language: Language,
        count: Number,
    ): String

    fun sanitizeCountInput(value: String): String

    fun parseCount(value: String): Number

    fun sections(
        inputs: ChecklistInputs,
        tab: ChecklistTab,
        enabledPluralForms: Set<PluralForm>,
    ): List<ChecklistSection>
}
