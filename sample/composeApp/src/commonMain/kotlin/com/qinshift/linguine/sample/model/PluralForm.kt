package com.qinshift.linguine.sample.model

internal enum class PluralForm(
    val key: String,
) {
    Zero(key = "zero"),
    One(key = "one"),
    Two(key = "two"),
    Few(key = "few"),
    Many(key = "many"),
    Other(key = "other");

    companion object {
        val mandatory: PluralForm = Other
    }
}
