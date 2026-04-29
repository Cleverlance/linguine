package com.qinshift.linguine.linguineruntime.presentation

import co.touchlab.kermit.Logger

internal interface Localisation {

    fun get(key: String, vararg args: String): String?

    fun getPlural(key: String, count: Number, vararg args: String): String?

    object Empty : Localisation {
        override fun get(key: String, vararg args: String): String? = null
        override fun getPlural(key: String, count: Number, vararg args: String): String? = null
    }

    object KeyBased : Localisation {
        override fun get(key: String, vararg args: String) = key.also {
            Logger.w { "No localisation found for $key" }
        }

        override fun getPlural(key: String, count: Number, vararg args: String) = key.also {
            Logger.w { "No plural localisation found for $key" }
        }
    }

    data class MapBased(
        private val language: Language,
        private val map: Map<String, LocalisationValue>,
    ) : Localisation {
        override fun get(key: String, vararg args: String): String? {
            val value = map[key] as? LocalisationValue.Text ?: return null
            return format(value.value, args.toList())
        }

        override fun getPlural(key: String, count: Number, vararg args: String): String? {
            val value = map[key] as? LocalisationValue.Plural ?: return null
            val selectedForms = LanguagePluralRules.select(language, count)
            val template = value.forms.resolvePluralForm(selectedForms)
                ?: return null

            return format(template, args.toList())
        }

        private fun format(template: String, args: List<String>): String {
            var nextUnindexedArgument = 0
            return FORMAT_SPECIFIER_REGEX.replace(template) { match ->
                val explicitIndex = match.groupValues[1]
                    .takeIf(String::isNotEmpty)
                    ?.toInt()
                val argumentIndex = explicitIndex?.dec() ?: nextUnindexedArgument++

                args.getOrNull(argumentIndex) ?: match.value
            }
        }

        private companion object {
            val FORMAT_SPECIFIER_REGEX = Regex("%(?:([0-9]+)\\$)?[sdf]")
        }
    }
}
