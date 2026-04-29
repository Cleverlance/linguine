package com.qinshift.linguine.linguineruntime.presentation

import co.touchlab.kermit.Logger
import kotlin.native.concurrent.ThreadLocal
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@ThreadLocal
internal object LocalisationProvider {

    private const val DEFAULT_LANGUAGE_CODE = "en"

    private var defaultLanguage: Language = Language(DEFAULT_LANGUAGE_CODE)
    private var isDefaultLanguageExplicitlySet: Boolean = false
    private var hasWarnedAboutImplicitDefaultLanguage: Boolean = false
    private var cachedSystemLanguage: Language? = null
    private var cachedSystemLocalisation: Localisation = Localisation.Empty
    private var cachedDefaultLanguage: Language? = null
    private var cachedDefaultLocalisation: Localisation = Localisation.Empty

    fun configureDefaultLanguage(code: String) {
        require(code.isNotBlank()) {
            "Default localization language code must not be blank."
        }

        val language = Language(code.trim().lowercase())
        isDefaultLanguageExplicitlySet = true
        hasWarnedAboutImplicitDefaultLanguage = false
        if (language == defaultLanguage) return
        defaultLanguage = language
        cachedDefaultLanguage = null
        cachedDefaultLocalisation = Localisation.Empty
    }

    fun system(language: Language): Localisation {
        return if (language == cachedSystemLanguage) {
            cachedSystemLocalisation
        } else {
            provideAndCacheSystem(language)
        }
    }

    fun default(): Localisation {
        return if (defaultLanguage == cachedDefaultLanguage) {
            cachedDefaultLocalisation
        } else {
            provideAndCacheDefault()
        }
    }

    private fun provideAndCacheSystem(language: Language): Localisation {
        // Order is critical: provide() must succeed before cache keys are updated.
        // Updating the language first creates a stale cache entry after parsing failures.
        val localisation = provide(language)
        cachedSystemLanguage = language
        cachedSystemLocalisation = localisation
        return localisation
    }

    private fun provideAndCacheDefault(): Localisation {
        // Order is critical: provide() must succeed before cache keys are updated.
        // Updating the language first creates a stale cache entry after parsing failures.
        val localisation = provide(
            localization = null,
            resolvedLanguage = defaultLanguage,
        )
        cachedDefaultLanguage = defaultLanguage
        cachedDefaultLocalisation = localisation
        return localisation
    }

    private fun provide(localization: Language) =
        provide(localization = localization, resolvedLanguage = localization)

    private fun provide(localization: Language?, resolvedLanguage: Language): Localisation {
        if (localization == null && !isDefaultLanguageExplicitlySet && !hasWarnedAboutImplicitDefaultLanguage) {
            Logger.w {
                """
                    Linguine default localization language was not configured explicitly. 
                    The fallback file strings.json will be interpreted as English ('en'). 
                    If your fallback/source localization uses a different language, 
                    call Localiser.configureDefaultLocalization("<language-code>") during application startup. 
                    Currently supported languages are: ${LanguagePluralSupported.supportedCodes}.
                """.trimIndent()
            }
            hasWarnedAboutImplicitDefaultLanguage = true
        }

        return LocalisationRetriever.getJson(localization = localization)
            .parse(resolvedLanguage)
    }

    @Suppress("StringLiteralDuplication", "ReturnCount")
    private fun String?.parse(language: Language): Localisation {
        return if (this == null) {
            Localisation.Empty
        } else {
            try {
                val parsed = Json.parseToJsonElement(this)
                    .jsonObject
                    .mapValues { (key, value) -> parseValue(key, value) }

                Localisation.MapBased(language = language, map = parsed)
            } catch (exception: SerializationException) {
                Logger.e(exception) { "Parsing of the localisation JSON failed." }
                return Localisation.Empty
            }
        }
    }

    private fun parseValue(key: String, value: JsonElement): LocalisationValue {
        return when (value) {
            is JsonPrimitive -> {
                require(value.isString) { "Expected localisation value for key '$key' to be a string." }
                LocalisationValue.Text(value.content)
            }

            is JsonObject -> {
                val forms = value.mapValues { (pluralKey, pluralValue) ->
                    val primitive = pluralValue.jsonPrimitive
                    require(primitive.isString) {
                        "Expected plural form '$pluralKey' of key '$key' to be a string."
                    }
                    primitive.content
                }
                require(LanguagePluralForm.OTHER.key in forms) {
                    "Expected plural localisation value for key '$key' to contain required fallback form 'other'."
                }
                LocalisationValue.Plural(LocalisationForms(forms))
            }

            else -> {
                error("Expected localisation value for key '$key' to be a string or object.")
            }
        }
    }
}
