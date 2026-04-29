package com.qinshift.linguine.linguineruntime.presentation

internal enum class LanguagePluralSupported(val code: String) {
    CZECH("cs"),
    ENGLISH("en"),
    FRENCH("fr"),
    SLOVAK("sk"),
    SPANISH("es");

    companion object {
        private val byCode = entries.associateBy(LanguagePluralSupported::code)
        val supportedCodes: String = entries.joinToString(", ") { it.code }

        fun fromCode(code: String): LanguagePluralSupported? = byCode[code.lowercase()]
    }
}
