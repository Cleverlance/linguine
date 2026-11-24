@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.qinshift.linguine.sample.system

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.qinshift.linguine.sample.model.PluralForm
import com.qinshift.linguine.sample.presentation.ChecklistState

@Composable
internal fun PluralFormControls(
    options: List<ChecklistState.PluralFormOption>,
    onPluralFormToggled: (PluralForm) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "Plural form fallback simulation",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
            )
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "Disable a plural form to preview the next available fallback in the row details. " +
                    "PASS and FAIL compare the Linguine result with the original expected text.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(options) { option ->
                    FilterChip(
                        selected = option.selected,
                        enabled = option.enabled,
                        onClick = { onPluralFormToggled(option.form) },
                        label = { Text(option.label) },
                    )
                }
            }
        }
    }
}
