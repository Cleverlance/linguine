package com.qinshift.linguine.sample.system

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.qinshift.linguine.sample.model.ChecklistStatus
import com.qinshift.linguine.sample.presentation.ChecklistState

@Composable
internal fun ChecklistRowItem(
    row: ChecklistState.ChecklistRowState,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier
                .clickable(onClick = onToggle)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = row.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = row.actual,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                StatusPill(
                    status = row.status,
                    label = row.statusLabel,
                )
            }
            if (expanded) {
                Divider()
                DetailGrid(row)
            }
        }
    }
}

@Composable
private fun StatusPill(
    status: ChecklistStatus,
    label: String,
    modifier: Modifier = Modifier,
) {
    val color = when (status) {
        ChecklistStatus.Passed -> Color(0xFF2E7D32)
        ChecklistStatus.Failed -> Color(0xFFC62828)
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = color,
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun DetailGrid(
    row: ChecklistState.ChecklistRowState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        row.count?.let { count ->
            val countValue = row.pluralCategoryText?.let { category ->
                "$count ($category)"
            } ?: count.toString()

            DetailLine(label = "Count", value = countValue)
        }
        DetailLine(label = "Template", value = row.template)
        DetailLine(label = "Expected", value = row.expected)
        DetailLine(label = "Actual", value = row.actual)
        row.simulatedFallback?.let { fallback ->
            DetailLine(label = "Simulated fallback", value = fallback)
        }
    }
}

@Composable
private fun DetailLine(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = if (label == "Template") FontFamily.Monospace else FontFamily.Default,
        )
    }
}
