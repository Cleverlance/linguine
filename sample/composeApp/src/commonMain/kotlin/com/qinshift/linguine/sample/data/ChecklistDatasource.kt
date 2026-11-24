package com.qinshift.linguine.sample.data

import com.qinshift.linguine.sample.model.ChecklistCallResult
import com.qinshift.linguine.sample.model.ChecklistInputs
import com.qinshift.linguine.sample.model.ChecklistRow
import com.qinshift.linguine.sample.model.ChecklistSectionId
import com.qinshift.linguine.sample.model.PluralForm

internal interface ChecklistDatasource {
    val id: ChecklistSectionId

    fun rows(
        inputs: ChecklistInputs,
        enabledPluralForms: Set<PluralForm>,
    ): List<ChecklistRow>

    fun resolve(block: () -> String): ChecklistCallResult =
        runCatching(block)
            .fold(
                onSuccess = ChecklistCallResult::Success,
                onFailure = { exception ->
                    ChecklistCallResult.Error(exception.toChecklistErrorText())
                },
            )

    private fun Throwable.toChecklistErrorText(): String {
        val message = message
            ?.lineSequence()
            ?.firstOrNull()
            ?.trim()
            ?: "unknown"

        return "ERROR: $message"
    }
}
