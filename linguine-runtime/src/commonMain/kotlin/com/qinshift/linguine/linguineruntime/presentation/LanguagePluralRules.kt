package com.qinshift.linguine.linguineruntime.presentation

import kotlin.math.abs

// some of the rules can be found here: https://localizely.com/language-plural-rules/
internal object LanguagePluralRules {

    fun select(language: Language, count: Number): List<LanguagePluralForm> {
        val absoluteCount = abs(count.toDouble())
        require(absoluteCount.isFinite()) { "Plural count must be finite." }

        return when (LanguagePluralSupported.fromCode(language.code)) {
            LanguagePluralSupported.CZECH -> selectCzech(absoluteCount)
            LanguagePluralSupported.ENGLISH -> selectEnglish(absoluteCount)
            LanguagePluralSupported.SPANISH -> selectSpanish(absoluteCount)
            LanguagePluralSupported.FRENCH -> selectFrench(absoluteCount)
            LanguagePluralSupported.SLOVAK -> selectSlovak(absoluteCount)
            else -> error(
                """
                    Plural rules for language '${language.code}' are not implemented in Linguine. 
                    Add the language-specific plural rules to the plugin/runtime 
                    before using plural localisations for this language.
                """.trimIndent(),
            )
        }
    }

    private fun selectEnglish(count: Double): List<LanguagePluralForm> {
        return when {
            count.isWholeNumber(0L) -> listOf(LanguagePluralForm.ZERO, LanguagePluralForm.OTHER)
            count.isWholeNumber(1L) -> listOf(LanguagePluralForm.ONE)
            count.isWholeNumber(2L) -> listOf(LanguagePluralForm.TWO, LanguagePluralForm.OTHER)
            else -> listOf(LanguagePluralForm.OTHER)
        }
    }

    @Suppress("MagicNumber")
    private fun selectCzech(count: Double): List<LanguagePluralForm> {
        return when {
            count.isWholeNumber(0L) -> listOf(LanguagePluralForm.ZERO, LanguagePluralForm.OTHER)
            count.hasNonZeroFraction() -> listOf(LanguagePluralForm.MANY)
            count.isWholeNumber(1L) -> listOf(LanguagePluralForm.ONE)
            count.isWholeNumber(2L) -> listOf(LanguagePluralForm.TWO, LanguagePluralForm.FEW)
            count.toLong() in 3L..4L -> listOf(LanguagePluralForm.FEW)
            else -> listOf(LanguagePluralForm.OTHER)
        }
    }

    @Suppress("MagicNumber")
    private fun selectSlovak(count: Double): List<LanguagePluralForm> {
        return when {
            count.isWholeNumber(0L) -> listOf(LanguagePluralForm.ZERO, LanguagePluralForm.OTHER)
            count.hasNonZeroFraction() -> listOf(LanguagePluralForm.MANY)
            count.isWholeNumber(1L) -> listOf(LanguagePluralForm.ONE)
            count.isWholeNumber(2L) -> listOf(LanguagePluralForm.TWO, LanguagePluralForm.FEW)
            count in 3.0..4.0 -> listOf(LanguagePluralForm.FEW)
            else -> listOf(LanguagePluralForm.OTHER)
        }
    }

    @Suppress("MagicNumber")
    private fun selectFrench(count: Double): List<LanguagePluralForm> {
        return when {
            count.isWholeNumber(0L) -> listOf(LanguagePluralForm.ZERO, LanguagePluralForm.ONE)
            count in 0.0..<2.0 -> listOf(LanguagePluralForm.ONE)
            count.isWholeNumber(2L) -> listOf(LanguagePluralForm.TWO, LanguagePluralForm.OTHER)
            count.isWholeMillion() -> listOf(LanguagePluralForm.MANY)
            else -> listOf(LanguagePluralForm.OTHER)
        }
    }

    @Suppress("MagicNumber")
    private fun selectSpanish(count: Double): List<LanguagePluralForm> {
        return when {
            count.isWholeNumber(0L) -> listOf(LanguagePluralForm.ZERO, LanguagePluralForm.OTHER)
            count.isWholeNumber(1L) -> listOf(LanguagePluralForm.ONE)
            count.isWholeNumber(2L) -> listOf(LanguagePluralForm.TWO, LanguagePluralForm.OTHER)
            count.isWholeMillion() -> listOf(LanguagePluralForm.MANY)
            else -> listOf(LanguagePluralForm.OTHER)
        }
    }

    private fun Double.isWholeNumber(expectedValue: Long): Boolean {
        return hasNonZeroFraction().not() && this.toLong() == expectedValue
    }

    @Suppress("MagicNumber")
    private fun Double.hasNonZeroFraction(): Boolean {
        return this % 1.0 != 0.0
    }

    @Suppress("MagicNumber")
    private fun Double.isWholeMillion(): Boolean {
        return hasNonZeroFraction().not() && this.toLong() != 0L && this.toLong() % 1_000_000L == 0L
    }
}
