package com.qinshift.linguine.sample.data

import com.qinshift.linguine.sample.model.Language
import com.qinshift.linguine.sample.model.PluralForm
import kotlin.math.abs

internal class LanguagePluralRulesDatasource {

    fun selectCategory(
        language: Language,
        count: Number,
    ): PluralForm = selectCategories(language, count).first()

    fun selectCategories(
        language: Language,
        count: Number,
    ): List<PluralForm> {
        val absoluteCount = abs(count.toDouble())

        return when (language) {
            Language.English -> selectEnglish(absoluteCount)
            Language.Czech -> selectCzech(absoluteCount)
            Language.Spanish -> selectSpanish(absoluteCount)
            Language.French -> selectFrench(absoluteCount)
            Language.Slovak -> selectSlovak(absoluteCount)
            Language.Ukrainian -> listOf(PluralForm.Other)
        }
    }

    fun subjectForm(
        language: Language,
        count: Number,
    ): String {
        val category = selectCategory(language, count)

        return when (language) {
            Language.English,
            Language.Czech,
            Language.Spanish,
            Language.French,
            Language.Slovak,
            -> subjectForms.getValue(language).getValue(category)

            Language.Ukrainian -> count.selectUkrainianSubjectForm()
        }
    }

    fun hasUnsupportedPluralRules(language: Language): Boolean {
        return language == Language.Ukrainian
    }

    fun unsupportedPluralRulesMessage(language: Language): String =
        "ERROR: Plural rules for language '${language.code}' are not implemented in Linguine."

    private fun selectEnglish(count: Double): List<PluralForm> {
        return when {
            count.isWholeNumber(0L) -> listOf(PluralForm.Zero, PluralForm.Other)
            count.isWholeNumber(1L) -> listOf(PluralForm.One)
            count.isWholeNumber(2L) -> listOf(PluralForm.Two, PluralForm.Other)
            else -> listOf(PluralForm.Other)
        }
    }

    @Suppress("MagicNumber")
    private fun selectCzech(count: Double): List<PluralForm> {
        return when {
            count.isWholeNumber(0L) -> listOf(PluralForm.Zero, PluralForm.Other)
            count.hasNonZeroFraction() -> listOf(PluralForm.Many)
            count.isWholeNumber(1L) -> listOf(PluralForm.One)
            count.isWholeNumber(2L) -> listOf(PluralForm.Two, PluralForm.Few)
            count.toLong() in 3L..4L -> listOf(PluralForm.Few)
            else -> listOf(PluralForm.Other)
        }
    }

    @Suppress("MagicNumber")
    private fun selectSpanish(count: Double): List<PluralForm> {
        return when {
            count.isWholeNumber(0L) -> listOf(PluralForm.Zero, PluralForm.Other)
            count.isWholeNumber(1L) -> listOf(PluralForm.One)
            count.isWholeNumber(2L) -> listOf(PluralForm.Two, PluralForm.Other)
            count.isWholeMillion() -> listOf(PluralForm.Many)
            else -> listOf(PluralForm.Other)
        }
    }

    @Suppress("MagicNumber")
    private fun selectFrench(count: Double): List<PluralForm> {
        return when {
            count.isWholeNumber(0L) -> listOf(PluralForm.Zero, PluralForm.One)
            count in 0.0..<2.0 -> listOf(PluralForm.One)
            count.isWholeNumber(2L) -> listOf(PluralForm.Two, PluralForm.Other)
            count.isWholeMillion() -> listOf(PluralForm.Many)
            else -> listOf(PluralForm.Other)
        }
    }

    @Suppress("MagicNumber")
    private fun selectSlovak(count: Double): List<PluralForm> {
        return when {
            count.isWholeNumber(0L) -> listOf(PluralForm.Zero, PluralForm.Other)
            count.hasNonZeroFraction() -> listOf(PluralForm.Many)
            count.isWholeNumber(1L) -> listOf(PluralForm.One)
            count.isWholeNumber(2L) -> listOf(PluralForm.Two, PluralForm.Few)
            count in 3.0..4.0 -> listOf(PluralForm.Few)
            else -> listOf(PluralForm.Other)
        }
    }

    private fun Number.selectUkrainianSubjectForm(): String {
        val absoluteValue = abs(toDouble())
        if (absoluteValue.hasNonZeroFraction()) return "діда"

        val value = absoluteValue.toLong()
        val lastDigit = value % 10
        val lastTwoDigits = value % 100

        return when {
            lastDigit == 1L && lastTwoDigits != 11L -> "дід"
            lastDigit in 2L..4L && lastTwoDigits !in 12L..14L -> "діди"
            else -> "дідів"
        }
    }

    private fun Double.isWholeNumber(expectedValue: Long): Boolean {
        return hasNonZeroFraction().not() && toLong() == expectedValue
    }

    @Suppress("MagicNumber")
    private fun Double.hasNonZeroFraction(): Boolean {
        return this % 1.0 != 0.0
    }

    @Suppress("MagicNumber")
    private fun Double.isWholeMillion(): Boolean {
        return hasNonZeroFraction().not() && toLong() != 0L && toLong() % 1_000_000L == 0L
    }

    private companion object {
        private val subjectForms = mapOf(
            Language.English to mapOf(
                PluralForm.Zero to "grandfathers",
                PluralForm.One to "grandfather",
                PluralForm.Two to "grandfathers",
                PluralForm.Few to "grandfathers",
                PluralForm.Many to "grandfathers",
                PluralForm.Other to "grandfathers",
            ),
            Language.Czech to mapOf(
                PluralForm.Zero to "dědů",
                PluralForm.One to "děda",
                PluralForm.Two to "dědové",
                PluralForm.Few to "dědové",
                PluralForm.Many to "dědy",
                PluralForm.Other to "dědů",
            ),
            Language.Spanish to mapOf(
                PluralForm.Zero to "abuelos",
                PluralForm.One to "abuelo",
                PluralForm.Two to "abuelos",
                PluralForm.Few to "abuelos",
                PluralForm.Many to "de abuelos",
                PluralForm.Other to "abuelos",
            ),
            Language.French to mapOf(
                PluralForm.Zero to "grand-père",
                PluralForm.One to "grand-père",
                PluralForm.Two to "grands-pères",
                PluralForm.Few to "grands-pères",
                PluralForm.Many to "de grands-pères",
                PluralForm.Other to "grands-pères",
            ),
            Language.Slovak to mapOf(
                PluralForm.Zero to "dedov",
                PluralForm.One to "dedo",
                PluralForm.Two to "dedovia",
                PluralForm.Few to "dedovia",
                PluralForm.Many to "deda",
                PluralForm.Other to "dedov",
            ),
        )
    }
}
