package com.qinshift.linguine.linguineruntime.presentation

internal sealed interface LocalisationValue {
    data class Text(val value: String) : LocalisationValue
    data class Plural(val forms: LocalisationForms) : LocalisationValue
}
