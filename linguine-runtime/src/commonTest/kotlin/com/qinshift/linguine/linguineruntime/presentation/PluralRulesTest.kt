package com.qinshift.linguine.linguineruntime.presentation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PluralRulesTest {

    @Test
    fun `select chooses english forms`() {
        assertEquals(listOf(LanguagePluralForm.ZERO, LanguagePluralForm.OTHER), LanguagePluralRules.select(Language("en"), 0))
        assertEquals(listOf(LanguagePluralForm.ONE), LanguagePluralRules.select(Language("en"), 1))
        assertEquals(listOf(LanguagePluralForm.TWO, LanguagePluralForm.OTHER), LanguagePluralRules.select(Language("en"), 2))
        assertEquals(listOf(LanguagePluralForm.ZERO, LanguagePluralForm.OTHER), LanguagePluralRules.select(Language("en"), 0.0))
        assertEquals(listOf(LanguagePluralForm.ONE), LanguagePluralRules.select(Language("en"), 1.0))
        assertEquals(listOf(LanguagePluralForm.OTHER), LanguagePluralRules.select(Language("en"), 1.5))
    }

    @Test
    fun `select chooses czech forms`() {
        assertEquals(listOf(LanguagePluralForm.ZERO, LanguagePluralForm.OTHER), LanguagePluralRules.select(Language("cs"), 0))
        assertEquals(listOf(LanguagePluralForm.ONE), LanguagePluralRules.select(Language("cs"), 1))
        assertEquals(listOf(LanguagePluralForm.TWO, LanguagePluralForm.FEW), LanguagePluralRules.select(Language("cs"), 2))
        assertEquals(listOf(LanguagePluralForm.FEW), LanguagePluralRules.select(Language("cs"), 3))
        assertEquals(listOf(LanguagePluralForm.FEW), LanguagePluralRules.select(Language("cs"), 4))
        assertEquals(listOf(LanguagePluralForm.OTHER), LanguagePluralRules.select(Language("cs"), 5))
        assertEquals(listOf(LanguagePluralForm.ZERO, LanguagePluralForm.OTHER), LanguagePluralRules.select(Language("cs"), 0.0))
        assertEquals(listOf(LanguagePluralForm.ONE), LanguagePluralRules.select(Language("cs"), 1.0))
        assertEquals(listOf(LanguagePluralForm.OTHER), LanguagePluralRules.select(Language("cs"), 10.0))
        assertEquals(listOf(LanguagePluralForm.MANY), LanguagePluralRules.select(Language("cs"), 1.5))
        assertEquals(listOf(LanguagePluralForm.MANY), LanguagePluralRules.select(Language("cs"), 0.5))
    }

    @Test
    fun `select chooses slovak forms`() {
        assertEquals(listOf(LanguagePluralForm.ZERO, LanguagePluralForm.OTHER), LanguagePluralRules.select(Language("sk"), 0))
        assertEquals(listOf(LanguagePluralForm.ONE), LanguagePluralRules.select(Language("sk"), 1))
        assertEquals(listOf(LanguagePluralForm.TWO, LanguagePluralForm.FEW), LanguagePluralRules.select(Language("sk"), 2))
        assertEquals(listOf(LanguagePluralForm.FEW), LanguagePluralRules.select(Language("sk"), 3))
        assertEquals(listOf(LanguagePluralForm.FEW), LanguagePluralRules.select(Language("sk"), 4))
        assertEquals(listOf(LanguagePluralForm.OTHER), LanguagePluralRules.select(Language("sk"), 5))
        assertEquals(listOf(LanguagePluralForm.ZERO, LanguagePluralForm.OTHER), LanguagePluralRules.select(Language("sk"), 0.0))
        assertEquals(listOf(LanguagePluralForm.ONE), LanguagePluralRules.select(Language("sk"), 1.0))
        assertEquals(listOf(LanguagePluralForm.OTHER), LanguagePluralRules.select(Language("sk"), 10.0))
        assertEquals(listOf(LanguagePluralForm.MANY), LanguagePluralRules.select(Language("sk"), 1.5))
        assertEquals(listOf(LanguagePluralForm.MANY), LanguagePluralRules.select(Language("sk"), 0.5))
    }

    @Test
    fun `select chooses french forms`() {
        assertEquals(listOf(LanguagePluralForm.ZERO, LanguagePluralForm.ONE), LanguagePluralRules.select(Language("fr"), 0))
        assertEquals(listOf(LanguagePluralForm.ONE), LanguagePluralRules.select(Language("fr"), 1))
        assertEquals(listOf(LanguagePluralForm.ZERO, LanguagePluralForm.ONE), LanguagePluralRules.select(Language("fr"), 0.0))
        assertEquals(listOf(LanguagePluralForm.ONE), LanguagePluralRules.select(Language("fr"), 1.0))
        assertEquals(listOf(LanguagePluralForm.ONE), LanguagePluralRules.select(Language("fr"), 1.5))
        assertEquals(listOf(LanguagePluralForm.TWO, LanguagePluralForm.OTHER), LanguagePluralRules.select(Language("fr"), 2))
        assertEquals(listOf(LanguagePluralForm.TWO, LanguagePluralForm.OTHER), LanguagePluralRules.select(Language("fr"), 2.0))
        assertEquals(listOf(LanguagePluralForm.MANY), LanguagePluralRules.select(Language("fr"), 1_000_000))
    }

    @Test
    fun `select chooses spanish forms`() {
        assertEquals(listOf(LanguagePluralForm.ZERO, LanguagePluralForm.OTHER), LanguagePluralRules.select(Language("es"), 0))
        assertEquals(listOf(LanguagePluralForm.ONE), LanguagePluralRules.select(Language("es"), 1))
        assertEquals(listOf(LanguagePluralForm.TWO, LanguagePluralForm.OTHER), LanguagePluralRules.select(Language("es"), 2))
        assertEquals(listOf(LanguagePluralForm.ZERO, LanguagePluralForm.OTHER), LanguagePluralRules.select(Language("es"), 0.0))
        assertEquals(listOf(LanguagePluralForm.ONE), LanguagePluralRules.select(Language("es"), 1.0))
        assertEquals(listOf(LanguagePluralForm.OTHER), LanguagePluralRules.select(Language("es"), 1.5))
        assertEquals(listOf(LanguagePluralForm.MANY), LanguagePluralRules.select(Language("es"), 1_000_000))
    }

    @Test
    fun `select uses absolute value for negative counts`() {
        assertEquals(listOf(LanguagePluralForm.ONE), LanguagePluralRules.select(Language("en"), -1))
        assertEquals(listOf(LanguagePluralForm.TWO, LanguagePluralForm.FEW), LanguagePluralRules.select(Language("cs"), -2))
        assertEquals(listOf(LanguagePluralForm.FEW), LanguagePluralRules.select(Language("sk"), -4))
        assertEquals(listOf(LanguagePluralForm.MANY), LanguagePluralRules.select(Language("cs"), -1.5))
    }

    @Test
    fun `select fails for non finite count`() {
        assertFailsWith<IllegalArgumentException> {
            LanguagePluralRules.select(Language("en"), Double.NaN)
        }
        assertFailsWith<IllegalArgumentException> {
            LanguagePluralRules.select(Language("en"), Double.POSITIVE_INFINITY)
        }
        assertFailsWith<IllegalArgumentException> {
            LanguagePluralRules.select(Language("en"), Double.NEGATIVE_INFINITY)
        }
    }

    @Test
    fun `resolvePluralForm falls back to other when selected form is missing`() {
        val forms = mapOf(
            "one" to "%1\$s item",
            "other" to "%1\$s items",
        )
        val localisationForms = LocalisationForms(forms)
        assertEquals("%1\$s items", localisationForms.resolvePluralForm(listOf(LanguagePluralForm.FEW)))
    }

    @Test
    fun `resolvePluralForm can return any unicode plural form when present`() {
        val forms = mapOf(
            "zero" to "zero form",
            "other" to "other form",
            "two" to "two form",
            "many" to "many form",
        )
        val localisationForms = LocalisationForms(forms)
        assertEquals("zero form", localisationForms.resolvePluralForm(listOf(LanguagePluralForm.ZERO)))
        assertEquals("two form", localisationForms.resolvePluralForm(listOf(LanguagePluralForm.TWO)))
        assertEquals("many form", localisationForms.resolvePluralForm(listOf(LanguagePluralForm.MANY)))
    }

    @Test
    fun `resolvePluralForm uses first available selected form`() {
        val forms = mapOf(
            "one" to "one form",
            "other" to "other form",
        )
        val localisationForms = LocalisationForms(forms)
        assertEquals(
            "one form",
            localisationForms.resolvePluralForm(listOf(LanguagePluralForm.ZERO, LanguagePluralForm.ONE)),
        )
    }

    @Test
    fun `resolvePluralForm returns null when other fallback is missing`() {
        val forms = mapOf(
            "one" to "one form",
            "few" to "few form",
        )
        val localisationForms = LocalisationForms(forms)
        assertEquals(null, localisationForms.resolvePluralForm(listOf(LanguagePluralForm.MANY)))
    }

    @Test
    fun `select fails for unsupported language`() {
        assertFailsWith<IllegalStateException> {
            LanguagePluralRules.select(Language("de"), 2)
        }
    }
}
