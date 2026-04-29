package com.qinshift.linguine.linguineruntime.presentation

import kotlin.jvm.JvmInline

@JvmInline
internal value class LocalisationForms(internal val values: Map<String, String>) {
    internal fun resolvePluralForm(selectedForms: List<LanguagePluralForm>): String? {
        return selectedForms.firstNotNullOfOrNull { form -> this.values[form.key] }
            ?: this.values[LanguagePluralForm.OTHER.key]
    }
}
