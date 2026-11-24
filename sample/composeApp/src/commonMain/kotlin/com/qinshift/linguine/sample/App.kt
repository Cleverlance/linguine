package com.qinshift.linguine.sample

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.qinshift.linguine.sample.data.ChecklistExpectedDatasource
import com.qinshift.linguine.sample.data.DefaultChecklistRepository
import com.qinshift.linguine.sample.data.LanguagePluralRulesDatasource
import com.qinshift.linguine.sample.data.PlainChecklistDatasource
import com.qinshift.linguine.sample.data.PluralAllFormsChecklistDatasource
import com.qinshift.linguine.sample.data.PluralOtherOnlyChecklistDatasource
import com.qinshift.linguine.sample.device.CurrentLanguageControllerImpl
import com.qinshift.linguine.sample.presentation.ChecklistViewModel
import com.qinshift.linguine.sample.system.ChecklistScreen

@Composable
internal fun App(
    modifier: Modifier = Modifier,
) {
    val languagePluralRulesDatasource = remember {
        LanguagePluralRulesDatasource()
    }
    val checklistExpectedDatasource = remember {
        ChecklistExpectedDatasource()
    }
    val plainChecklistDatasource = remember(checklistExpectedDatasource) {
        PlainChecklistDatasource(
            checklistExpectedDatasource = checklistExpectedDatasource,
        )
    }
    val pluralAllFormsChecklistDatasource = remember(
        checklistExpectedDatasource,
        languagePluralRulesDatasource,
    ) {
        PluralAllFormsChecklistDatasource(
            checklistExpectedDatasource = checklistExpectedDatasource,
            languagePluralRulesDatasource = languagePluralRulesDatasource,
        )
    }
    val pluralOtherOnlyChecklistDatasource = remember(
        checklistExpectedDatasource,
        languagePluralRulesDatasource,
    ) {
        PluralOtherOnlyChecklistDatasource(
            checklistExpectedDatasource = checklistExpectedDatasource,
            languagePluralRulesDatasource = languagePluralRulesDatasource,
        )
    }
    val repository = remember(
        plainChecklistDatasource,
        pluralAllFormsChecklistDatasource,
        pluralOtherOnlyChecklistDatasource,
        languagePluralRulesDatasource,
    ) {
        DefaultChecklistRepository(
            plainChecklistDatasource = plainChecklistDatasource,
            pluralAllFormsChecklistDatasource = pluralAllFormsChecklistDatasource,
            pluralOtherOnlyChecklistDatasource = pluralOtherOnlyChecklistDatasource,
            languagePluralRulesDatasource = languagePluralRulesDatasource,
        )
    }
    val currentLanguageController = remember {
        CurrentLanguageControllerImpl()
    }
    val viewModel = remember(
        repository,
        currentLanguageController,
    ) {
        ChecklistViewModel(
            repository = repository,
            currentLanguageController = currentLanguageController,
        )
    }

    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme(),
    ) {
        ChecklistScreen(
            viewModel = viewModel,
            modifier = modifier,
        )
    }
}
