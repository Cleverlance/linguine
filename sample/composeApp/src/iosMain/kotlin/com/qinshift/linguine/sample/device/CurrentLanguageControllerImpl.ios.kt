@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.qinshift.linguine.sample.device

import com.qinshift.linguine.linguineruntime.presentation.Localiser
import com.qinshift.linguine.sample.domain.CurrentLanguageController
import com.qinshift.linguine.sample.model.Language
import kotlinx.cinterop.BetaInteropApi
import platform.Foundation.NSString
import platform.Foundation.NSThread
import platform.Foundation.create

internal actual class CurrentLanguageControllerImpl actual constructor() : CurrentLanguageController {
    init {
        Localiser.configureDefaultLocalization(Language.English.code)
    }

    actual override fun <T> withLanguage(language: Language, block: () -> T): T {
        val threadDictionary = NSThread.currentThread.threadDictionary
        val previousLanguage = threadDictionary.objectForKey(languageKey)
        threadDictionary.setObject(language.code, forKey = languageKey)

        return try {
            block()
        } finally {
            if (previousLanguage == null) {
                threadDictionary.removeObjectForKey(languageKey)
            } else {
                threadDictionary.setObject(previousLanguage, forKey = languageKey)
            }
        }
    }

    private companion object {
        // Read by SampleLocaleOverride.swift in the sample's iOS app.
        @OptIn(BetaInteropApi::class)
        private val languageKey = NSString.create(string = "com.qinshift.linguine.sample.translationLanguage")
    }
}
