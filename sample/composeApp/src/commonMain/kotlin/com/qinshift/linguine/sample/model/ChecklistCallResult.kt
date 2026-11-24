package com.qinshift.linguine.sample.model

internal sealed interface ChecklistCallResult {
    val text: String

    data class Success(
        override val text: String,
    ) : ChecklistCallResult

    data class Error(
        override val text: String,
    ) : ChecklistCallResult
}
