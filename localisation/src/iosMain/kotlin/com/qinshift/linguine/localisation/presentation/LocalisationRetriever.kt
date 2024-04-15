@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.qinshift.linguine.localisation.presentation

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfURL

internal actual object LocalisationRetriever {
    @OptIn(ExperimentalForeignApi::class)
    actual fun getJson(localization: Language?): String? {
        val suffix = localization?.code?.let { "-$it" }.orEmpty()
        val url = NSBundle.mainBundle.URLForResource(
            name = "strings$suffix",
            withExtension = "json",
        )

        return url?.let {
            NSString.stringWithContentsOfURL(
                it,
                encoding = NSUTF8StringEncoding,
                error = null
            )
        }
    }
}