package com.qinshift.linguine.linguineruntime.presentation

@Suppress("unused")
object Localiser {

    fun localise(key: String, vararg args: String): String {
        return LocalisationProvider.system().get(key, *args)
            ?: LocalisationProvider.default.get(key, *args)
            ?: Localisation.KeyBased.get(key, *args)
    }
}
