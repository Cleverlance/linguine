@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.qinshift.linguine.sample.system

import androidx.compose.animation.core.animate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.qinshift.linguine.sample.model.Language
import com.qinshift.linguine.sample.presentation.ChecklistState

@Composable
internal fun rememberChecklistTopBarState(): ChecklistTopBarState = remember {
    ChecklistTopBarState()
}

internal class ChecklistTopBarState {
    var inputPanelHeightPx: Int by mutableStateOf(0)
        private set

    private var collapseOffsetPx: Float by mutableStateOf(0f)

    val collapseFraction: Float
        get() = if (inputPanelHeightPx > 0) {
            (collapseOffsetPx / inputPanelHeightPx.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }

    val nestedScrollConnection: NestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            if (inputPanelHeightPx <= 0 || available.y >= 0f) return Offset.Zero

            return updateCollapseOffset(delta = available.y)
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            if (inputPanelHeightPx <= 0 || available.y <= 0f) return Offset.Zero

            return updateCollapseOffset(delta = available.y)
        }

        override suspend fun onPostFling(
            consumed: Velocity,
            available: Velocity,
        ): Velocity {
            if (inputPanelHeightPx <= 0) return Velocity.Zero

            val maxOffset = inputPanelHeightPx.toFloat()
            val targetOffset = if (collapseOffsetPx < maxOffset / SNAP_THRESHOLD_DIVIDER) {
                0f
            } else {
                maxOffset
            }

            animate(
                initialValue = collapseOffsetPx,
                targetValue = targetOffset,
            ) { value, _ ->
                collapseOffsetPx = value.coerceIn(
                    minimumValue = 0f,
                    maximumValue = maxOffset,
                )
            }

            return Velocity.Zero
        }
    }

    fun onInputPanelMeasured(heightPx: Int) {
        if (heightPx <= 0) return

        inputPanelHeightPx = heightPx
        collapseOffsetPx = collapseOffsetPx.coerceAtMost(heightPx.toFloat())
    }

    private fun updateCollapseOffset(delta: Float): Offset {
        val previousOffset = collapseOffsetPx
        val nextOffset = (previousOffset - delta).coerceIn(
            minimumValue = 0f,
            maximumValue = inputPanelHeightPx.toFloat(),
        )
        collapseOffsetPx = nextOffset

        return Offset(x = 0f, y = previousOffset - nextOffset)
    }
}

@Composable
internal fun ChecklistTopBar(
    modifier: Modifier = Modifier,
    state: ChecklistTopBarState,
    languageOptions: List<ChecklistState.LanguageOption>,
    onLanguageSelected: (Language) -> Unit,
    countText: String,
    onCountTextChange: (String) -> Unit,
    countEnabled: Boolean,
    subjectForm: String,
    param1: String,
    onParam1Change: (String) -> Unit,
    param2: String,
    onParam2Change: (String) -> Unit,
    param3: String,
    onParam3Change: (String) -> Unit,
) {
    val inputPanelVisibility = (1f - state.collapseFraction).coerceIn(0f, 1f)
    val inputPanelHeight = with(LocalDensity.current) {
        state.inputPanelHeightPx.toDp() * inputPanelVisibility
    }

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "Linguine Checklist",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
            LanguageSelector(
                options = languageOptions,
                onLanguageSelected = onLanguageSelected,
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .then(
                        if (state.inputPanelHeightPx > 0) {
                            Modifier.height(inputPanelHeight)
                        } else {
                            Modifier
                        },
                    )
                    .clipToBounds()
                    .graphicsLayer {
                        alpha = inputPanelVisibility
                    },
            ) {
                InputPanel(
                    modifier = if (state.inputPanelHeightPx == 0) {
                        Modifier.onGloballyPositioned { coordinates ->
                            state.onInputPanelMeasured(coordinates.size.height)
                        }
                    } else {
                        Modifier
                    },
                    countText = countText,
                    onCountTextChange = onCountTextChange,
                    countEnabled = countEnabled,
                    subjectForm = subjectForm,
                    param1 = param1,
                    onParam1Change = onParam1Change,
                    param2 = param2,
                    onParam2Change = onParam2Change,
                    param3 = param3,
                    onParam3Change = onParam3Change,
                )
            }
        }
    }
}

@Composable
private fun LanguageSelector(
    modifier: Modifier = Modifier,
    options: List<ChecklistState.LanguageOption>,
    onLanguageSelected: (Language) -> Unit,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(options) { option ->
            FilterChip(
                selected = option.selected,
                onClick = { onLanguageSelected(option.language) },
                label = { Text(option.label) },
            )
        }
    }
}

@Composable
private fun InputPanel(
    modifier: Modifier = Modifier,
    countText: String,
    onCountTextChange: (String) -> Unit,
    countEnabled: Boolean,
    subjectForm: String,
    param1: String,
    onParam1Change: (String) -> Unit,
    param2: String,
    onParam2Change: (String) -> Unit,
    param3: String,
    onParam3Change: (String) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ChecklistTextField(
            modifier = Modifier.fillMaxWidth(),
            value = countText,
            onValueChange = onCountTextChange,
            label = "Count",
            enabled = countEnabled,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            placeholder = "0",
            suffix = {
                Text(
                    text = subjectForm,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
        )
        ChecklistTextField(
            modifier = Modifier.fillMaxWidth(),
            value = param1,
            onValueChange = onParam1Change,
            label = "Param 1",
        )
        ChecklistTextField(
            modifier = Modifier.fillMaxWidth(),
            value = param2,
            onValueChange = onParam2Change,
            label = "Param 2",
        )
        ChecklistTextField(
            modifier = Modifier.fillMaxWidth(),
            value = param3,
            onValueChange = onParam3Change,
            label = "Param 3",
        )
    }
}

@Composable
private fun ChecklistTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    placeholder: String? = null,
    suffix: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        keyboardOptions = keyboardOptions,
        suffix = suffix,
        singleLine = true,
    )
}

private const val SNAP_THRESHOLD_DIVIDER = 2f
