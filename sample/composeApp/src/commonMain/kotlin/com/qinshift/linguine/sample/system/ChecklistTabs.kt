@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.qinshift.linguine.sample.system

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.qinshift.linguine.sample.model.ChecklistTab
import com.qinshift.linguine.sample.presentation.ChecklistState

@Composable
internal fun ChecklistTabs(
    options: List<ChecklistState.ChecklistTabOption>,
    onTabSelected: (ChecklistTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    TabRow(
        modifier = modifier,
        selectedTabIndex = options.indexOfFirst(ChecklistState.ChecklistTabOption::selected)
            .coerceAtLeast(0),
    ) {
        options.forEach { option ->
            Tab(
                selected = option.selected,
                onClick = { onTabSelected(option.tab) },
                text = { Text(option.label) },
            )
        }
    }
}
