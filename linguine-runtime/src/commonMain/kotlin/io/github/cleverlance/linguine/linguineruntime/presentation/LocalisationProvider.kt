package io.github.cleverlance.linguine.linguineruntime.presentation

import co.touchlab.kermit.Logger
import kotlin.native.concurrent.ThreadLocal
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

@ThreadLocal
internal object LocalisationProvider {

    val default: Localisation by lazy { provide(localization = null) }

    private var cachedLanguage: Language? = null
    private var cachedLocalisation: Localisation = Localisation.Empty

    fun system(): Localisation {
        val currentLanguage = LanguageRepository.load()
        return if (currentLanguage == cachedLanguage) {
            cachedLocalisation
        } else {
            cachedLanguage = currentLanguage
            cachedLocalisation = provide(localization = currentLanguage)
            cachedLocalisation
        }
    }

    private fun provide(localization: Language?) =
        LocalisationRetriever.getJson(localization = localization)
            .parse()

    private fun String?.parse(): Localisation {
        return if (this == null) {
            Localisation.Empty
        } else {
            try {
                Json.decodeFromString<Map<String, String>>(this).let(Localisation::MapBased)
            } catch (exception: SerializationException) {
                Logger.e(exception) { "Parsing of the localisation JSON failed." }
                return Localisation.Empty
            }
        }
    }
}
