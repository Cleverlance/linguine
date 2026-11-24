@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.qinshift.linguine.sample.device

import com.qinshift.linguine.sample.domain.CurrentLanguageController
import com.qinshift.linguine.sample.model.Language

internal expect class CurrentLanguageControllerImpl() : CurrentLanguageController {
    override fun <T> withLanguage(language: Language, block: () -> T): T
}
