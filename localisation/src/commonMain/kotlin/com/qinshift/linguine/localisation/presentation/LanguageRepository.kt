@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.qinshift.linguine.localisation.presentation

internal expect object LanguageRepository {
    fun load(): Language
}
