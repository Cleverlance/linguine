@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.cleverlance.linguine.linguineruntime.presentation

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

internal actual object LanguageRepository {
    actual fun load(): Language {
        return Language(NSLocale.currentLocale.languageCode)
    }
}
