package com.qinshift.linguine.sample.system

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.qinshift.linguine.sample.model.ChecklistSectionId
import com.qinshift.linguine.sample.model.ChecklistTab
import com.qinshift.linguine.sample.presentation.ChecklistViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ChecklistScreen(
    modifier: Modifier = Modifier,
    viewModel: ChecklistViewModel,
) {
    val state = viewModel.state
    val topBarState = rememberChecklistTopBarState()

    Scaffold(
        modifier = modifier.nestedScroll(topBarState.nestedScrollConnection),
        topBar = {
            ChecklistTopBar(
                state = topBarState,
                languageOptions = state.languageOptions,
                onLanguageSelected = viewModel::onLanguageSelected,
                countText = state.countText,
                onCountTextChange = viewModel::onCountTextChanged,
                countEnabled = state.isCountEnabled,
                subjectForm = state.subjectForm,
                param1 = state.param1,
                onParam1Change = viewModel::onParam1Changed,
                param2 = state.param2,
                onParam2Change = viewModel::onParam2Changed,
                param3 = state.param3,
                onParam3Change = viewModel::onParam3Changed,
            )
        },
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            stickyHeader {
                ChecklistTabs(
                    options = state.tabOptions,
                    onTabSelected = viewModel::onTabSelected,
                )
            }

            state.sections.forEach { section ->
                item(key = "${section.id.key}-header") {
                    PolicyHeader(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        section = section,
                    )
                }
                if (state.selectedTab == ChecklistTab.Plural && section.id == ChecklistSectionId.OtherOnly) {
                    item(key = "${section.id.key}-plural-form-controls") {
                        PluralFormControls(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            options = state.pluralFormOptions,
                            onPluralFormToggled = viewModel::onPluralFormToggled,
                        )
                    }
                }
                items(
                    items = section.rows,
                    key = { row -> row.id },
                ) { row ->
                    ChecklistRowItem(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        row = row,
                        expanded = row.id in state.expandedRowIds,
                        onToggle = { viewModel.onRowToggled(row.id) },
                    )
                }
            }
        }
    }
}
