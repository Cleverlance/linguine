@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.qinshift.linguine.sample.device

import com.qinshift.linguine.linguineruntime.presentation.Localiser
import com.qinshift.linguine.sample.domain.CurrentLanguageController
import com.qinshift.linguine.sample.model.Language
import java.util.Locale

internal actual class CurrentLanguageControllerImpl actual constructor() : CurrentLanguageController {
    init {
        Localiser.configureDefaultLocalization(Language.English.code)
    }

    actual override fun <T> withLanguage(language: Language, block: () -> T): T {
        val locale = Locale.forLanguageTag(language.code)
        if (Locale.getDefault() != locale) {
            Locale.setDefault(locale)
        }
        return block()
    }
}
