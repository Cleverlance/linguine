package com.qinshift.linguine.sample.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.qinshift.linguine.sample.domain.CurrentLanguageController
import com.qinshift.linguine.sample.domain.ChecklistRepository
import com.qinshift.linguine.sample.model.ChecklistInputs
import com.qinshift.linguine.sample.model.ChecklistRow
import com.qinshift.linguine.sample.model.ChecklistRowId
import com.qinshift.linguine.sample.model.ChecklistSection
import com.qinshift.linguine.sample.model.ChecklistSectionId
import com.qinshift.linguine.sample.model.ChecklistStatus
import com.qinshift.linguine.sample.model.ChecklistTab
import com.qinshift.linguine.sample.model.Language
import com.qinshift.linguine.sample.model.PluralForm
import com.qinshift.linguine.sample.presentation.ChecklistState.PluralFormOption

internal class ChecklistViewModel(
    private val repository: ChecklistRepository,
    private val currentLanguageController: CurrentLanguageController,
) : ViewModel() {
    var state: ChecklistState by mutableStateOf(ChecklistState())
        private set

    init {
        val language = Language.English

        val defaultParams = repository.defaultParams(language)
        state = buildState(
            language = language,
            selectedTab = ChecklistTab.Plain,
            countText = "0",
            param1 = defaultParams.param1,
            param2 = defaultParams.param2,
            param3 = defaultParams.param3,
            enabledPluralForms = PluralForm.entries.toSet(),
            expandedRowIds = emptySet(),
        )
    }

    fun onLanguageSelected(language: Language) {
        val defaultParams = repository.defaultParams(language)
        state = buildState(
            language = language,
            selectedTab = state.selectedTab,
            countText = state.countText,
            param1 = defaultParams.param1,
            param2 = defaultParams.param2,
            param3 = defaultParams.param3,
            enabledPluralForms = state.enabledPluralForms,
            expandedRowIds = state.expandedRowIds,
        )
    }

    fun onTabSelected(tab: ChecklistTab) {
        state = buildState(
            language = state.selectedLanguage,
            selectedTab = tab,
            countText = state.countText,
            param1 = state.param1,
            param2 = state.param2,
            param3 = state.param3,
            enabledPluralForms = state.enabledPluralForms,
            expandedRowIds = state.expandedRowIds,
        )
    }

    fun onCountTextChanged(value: String) {
        state = buildState(
            language = state.selectedLanguage,
            selectedTab = state.selectedTab,
            countText = repository.sanitizeCountInput(value),
            param1 = state.param1,
            param2 = state.param2,
            param3 = state.param3,
            enabledPluralForms = state.enabledPluralForms,
            expandedRowIds = state.expandedRowIds,
        )
    }

    fun onParam1Changed(value: String) {
        state = buildState(
            language = state.selectedLanguage,
            selectedTab = state.selectedTab,
            countText = state.countText,
            param1 = value,
            param2 = state.param2,
            param3 = state.param3,
            enabledPluralForms = state.enabledPluralForms,
            expandedRowIds = state.expandedRowIds,
        )
    }

    fun onParam2Changed(value: String) {
        state = buildState(
            language = state.selectedLanguage,
            selectedTab = state.selectedTab,
            countText = state.countText,
            param1 = state.param1,
            param2 = value,
            param3 = state.param3,
            enabledPluralForms = state.enabledPluralForms,
            expandedRowIds = state.expandedRowIds,
        )
    }

    fun onParam3Changed(value: String) {
        state = buildState(
            language = state.selectedLanguage,
            selectedTab = state.selectedTab,
            countText = state.countText,
            param1 = state.param1,
            param2 = state.param2,
            param3 = value,
            enabledPluralForms = state.enabledPluralForms,
            expandedRowIds = state.expandedRowIds,
        )
    }

    fun onPluralFormToggled(form: PluralForm) {
        if (form == PluralForm.mandatory) return

        val enabledPluralForms = if (form in state.enabledPluralForms) {
            state.enabledPluralForms - form
        } else {
            state.enabledPluralForms + form
        }

        state = buildState(
            language = state.selectedLanguage,
            selectedTab = state.selectedTab,
            countText = state.countText,
            param1 = state.param1,
            param2 = state.param2,
            param3 = state.param3,
            enabledPluralForms = enabledPluralForms + PluralForm.mandatory,
            expandedRowIds = state.expandedRowIds,
        )
    }

    fun onRowToggled(rowId: String) {
        val expandedRowIds = if (rowId in state.expandedRowIds) {
            state.expandedRowIds - rowId
        } else {
            state.expandedRowIds + rowId
        }

        state = state.copy(expandedRowIds = expandedRowIds)
    }

    private fun buildState(
        language: Language,
        selectedTab: ChecklistTab,
        countText: String,
        param1: String,
        param2: String,
        param3: String,
        enabledPluralForms: Set<PluralForm>,
        expandedRowIds: Set<String>,
    ): ChecklistState {
        val count = repository.parseCount(countText)
        val inputs = ChecklistInputs(
            language = language,
            count = count,
            param1 = param1,
            param2 = param2,
            param3 = param3,
        )

        return ChecklistState(
            languageOptions = repository.languages.toOptions(language),
            selectedLanguage = language,
            tabOptions = ChecklistTab.entries.toOptions(selectedTab),
            selectedTab = selectedTab,
            countText = countText,
            isCountEnabled = selectedTab == ChecklistTab.Plural,
            subjectForm = repository.subjectForm(language, count),
            param1 = param1,
            param2 = param2,
            param3 = param3,
            pluralFormOptions = PluralForm.entries.toOptions(enabledPluralForms),
            sections = currentLanguageController.withLanguage(language) {
                repository.sections(
                    inputs = inputs,
                    tab = selectedTab,
                    enabledPluralForms = enabledPluralForms,
                ).toState()
            },
            expandedRowIds = expandedRowIds,
        )
    }

    private val ChecklistState.enabledPluralForms: Set<PluralForm>
        get() = pluralFormOptions
            .filter(PluralFormOption::selected)
            .map(PluralFormOption::form)
            .toSet()

    private fun List<Language>.toOptions(selectedLanguage: Language): List<ChecklistState.LanguageOption> = map { language ->
        ChecklistState.LanguageOption(
            language = language,
            label = language.label,
            selected = language == selectedLanguage,
        )
    }

    private fun List<ChecklistTab>.toOptions(selectedTab: ChecklistTab): List<ChecklistState.ChecklistTabOption> = map { tab ->
        ChecklistState.ChecklistTabOption(
            tab = tab,
            label = tab.label,
            selected = tab == selectedTab,
        )
    }

    private fun List<PluralForm>.toOptions(enabledPluralForms: Set<PluralForm>): List<PluralFormOption> = map { form ->
        PluralFormOption(
            form = form,
            label = form.label,
            selected = form in enabledPluralForms,
            enabled = form != PluralForm.mandatory,
        )
    }

    private fun List<ChecklistSection>.toState(): List<ChecklistState.ChecklistSectionState> = map { section ->
        ChecklistState.ChecklistSectionState(
            id = section.id,
            title = when (section.id) {
                ChecklistSectionId.Plain -> "Plain strings"
                ChecklistSectionId.AllForms -> "Policy: require all forms"
                ChecklistSectionId.OtherOnly -> "Policy: require `other` only"
            },
            rows = section.rows.map { row -> row.toState() },
        )
    }

    private fun ChecklistRow.toState(): ChecklistState.ChecklistRowState {
        return ChecklistState.ChecklistRowState(
            id = id,
            title = rowId.title,
            count = count,
            pluralCategoryText = pluralCategory?.key,
            template = template,
            expected = expected,
            actual = actual,
            simulatedFallback = simulatedFallback,
            status = status,
            statusLabel = status.label,
        )
    }

    private val Language.label: String
        get() = when (this) {
            Language.Czech -> "Czech"
            Language.English -> "English"
            Language.French -> "French"
            Language.Slovak -> "Slovak"
            Language.Spanish -> "Spanish"
            Language.Ukrainian -> "Ukrainian"
        }

    private val ChecklistTab.label: String
        get() = when (this) {
            ChecklistTab.Plain -> "Plain"
            ChecklistTab.Plural -> "Plural"
        }

    private val PluralForm.label: String
        get() = when (this) {
            PluralForm.Zero -> "Zero"
            PluralForm.One -> "One"
            PluralForm.Two -> "Two"
            PluralForm.Few -> "Few"
            PluralForm.Many -> "Many"
            PluralForm.Other -> "Other"
        }

    private val ChecklistStatus.label: String
        get() = when (this) {
            ChecklistStatus.Passed -> "PASS"
            ChecklistStatus.Failed -> "FAIL"
        }

    private val ChecklistRowId.title: String
        get() = when (this) {
            is ChecklistRowId.Plain -> when (this) {
                ChecklistRowId.Plain.Sentence -> "Plain sentence"
                ChecklistRowId.Plain.FallbackSentence -> "Fallback sentence (English)"
                ChecklistRowId.Plain.SinglePlaceholder -> "Single placeholder"
                ChecklistRowId.Plain.OrderedPlaceholders -> "Ordered placeholders"
                ChecklistRowId.Plain.ReversedPlaceholders -> "Reversed placeholders"
                ChecklistRowId.Plain.FallbackPlaceholders -> "Fallback placeholders (English)"
            }

            is ChecklistRowId.Plural -> when (this) {
                ChecklistRowId.Plural.AllFormsItemCount -> "Plural subject"
                ChecklistRowId.Plural.AllFormsSparsePlaceholders -> "Sparse plural placeholders"
                ChecklistRowId.Plural.AllFormsCategoryName -> "Count sentence"
                ChecklistRowId.Plural.AllFormsFallbackCount -> "Language fallback count sentence (English)"
                ChecklistRowId.Plural.OtherOnlyItemCount -> "Other-only plural subject"
                ChecklistRowId.Plural.OtherOnlyReversedPlaceholders -> "Other-only reversed placeholders"
                ChecklistRowId.Plural.OtherOnlyCategoryName -> "Other-only count sentence"
                ChecklistRowId.Plural.OtherOnlyFallbackCount -> "Language fallback count sentence (English)"
            }
        }
}
