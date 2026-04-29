package com.qinshift.linguine.linguineruntime.presentation

@Suppress("unused")
public object Localiser {

    public fun configureDefaultLocalization(languageCode: String) {
        LocalisationProvider.configureDefaultLanguage(languageCode)
    }

    public fun localise(key: String, vararg args: String): String {
        val currentLanguage = LanguageRepository.load()
        return LocalisationProvider.system(currentLanguage).get(key, *args)
            ?: LocalisationProvider.default().get(key, *args)
            ?: Localisation.KeyBased.get(key, *args)
    }

    public fun localisePlural(key: String, count: Number, vararg args: String): String {
        val currentLanguage = LanguageRepository.load()
        return LocalisationProvider.system(currentLanguage).getPlural(key, count, *args)
            ?: LocalisationProvider.default().getPlural(key, count, *args)
            ?: Localisation.KeyBased.getPlural(key, count, *args)
    }
}
