package com.qinshift.linguine.sample.domain

import com.qinshift.linguine.sample.model.Language

internal interface CurrentLanguageController {
    fun <T> withLanguage(language: Language, block: () -> T): T
}
