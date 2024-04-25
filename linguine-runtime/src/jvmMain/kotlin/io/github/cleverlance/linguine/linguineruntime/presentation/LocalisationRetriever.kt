@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.cleverlance.linguine.linguineruntime.presentation

internal actual object LocalisationRetriever {

    private val classLoader = this::class.java.classLoader!!

    actual fun getJson(localization: Language?): String? {
        val suffix = localization?.code?.let { "-$it" }.orEmpty()
        val fileName = "assets/strings$suffix.json"
        return classLoader.getResource(fileName)?.readText()
    }
}
