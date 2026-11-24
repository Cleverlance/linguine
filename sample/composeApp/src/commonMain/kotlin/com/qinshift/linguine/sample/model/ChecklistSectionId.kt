package com.qinshift.linguine.sample.model

internal enum class ChecklistSectionId(
    val key: String,
) {
    Plain(key = "plain"),
    AllForms(key = "all-forms"),
    OtherOnly(key = "other-only"),
}
