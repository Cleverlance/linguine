package com.qinshift.linguine.sample.presentation

import com.qinshift.linguine.sample.model.ChecklistSectionId
import com.qinshift.linguine.sample.model.ChecklistStatus
import com.qinshift.linguine.sample.model.ChecklistTab
import com.qinshift.linguine.sample.model.Language
import com.qinshift.linguine.sample.model.PluralForm

internal data class ChecklistState(
    val languageOptions: List<LanguageOption> = emptyList(),
    val selectedLanguage: Language = Language.English,
    val tabOptions: List<ChecklistTabOption> = emptyList(),
    val selectedTab: ChecklistTab = ChecklistTab.Plain,
    val countText: String = "",
    val isCountEnabled: Boolean = false,
    val subjectForm: String = "",
    val param1: String = "",
    val param2: String = "",
    val param3: String = "",
    val pluralFormOptions: List<PluralFormOption> = emptyList(),
    val sections: List<ChecklistSectionState> = emptyList(),
    val expandedRowIds: Set<String> = emptySet(),
) {

    internal data class LanguageOption(
        val language: Language,
        val label: String,
        val selected: Boolean,
    )

    internal data class ChecklistTabOption(
        val tab: ChecklistTab,
        val label: String,
        val selected: Boolean,
    )

    internal data class PluralFormOption(
        val form: PluralForm,
        val label: String,
        val selected: Boolean,
        val enabled: Boolean,
    )

    internal data class ChecklistSectionState(
        val id: ChecklistSectionId,
        val title: String,
        val rows: List<ChecklistRowState>,
    )

    internal data class ChecklistRowState(
        val id: String,
        val title: String,
        val count: Number?,
        val pluralCategoryText: String?,
        val template: String,
        val expected: String,
        val actual: String,
        val simulatedFallback: String?,
        val status: ChecklistStatus,
        val statusLabel: String,
    )

}
