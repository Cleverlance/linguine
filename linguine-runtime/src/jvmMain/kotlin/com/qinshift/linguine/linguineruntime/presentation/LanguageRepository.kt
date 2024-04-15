@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.qinshift.linguine.linguineruntime.presentation

import java.util.Locale

internal actual object LanguageRepository {
    actual fun load(): Language {
        return Language(Locale.getDefault().language)
    }
}
