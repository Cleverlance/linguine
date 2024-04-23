@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.cleverlance.linguine.linguineruntime.presentation

internal expect object LocalisationRetriever {
    fun getJson(localization: Language?): String?
}
