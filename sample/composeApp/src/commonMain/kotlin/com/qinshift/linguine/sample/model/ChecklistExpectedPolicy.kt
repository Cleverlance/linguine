package com.qinshift.linguine.sample.model

internal enum class ChecklistExpectedPolicy(
    val key: String,
) {
    AllForms(key = "allForms"),
    OtherOnly(key = "otherOnly"),
}
