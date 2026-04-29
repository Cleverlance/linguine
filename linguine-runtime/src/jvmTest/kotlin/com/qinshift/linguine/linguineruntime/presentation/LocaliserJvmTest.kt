package com.qinshift.linguine.linguineruntime.presentation

import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LocaliserJvmTest {

    @Test
    fun `fallback strings json uses explicitly configured default localization language`() {
        val previousLocale = Locale.getDefault()

        try {
            Locale.setDefault(Locale.forLanguageTag("de"))
            Localiser.configureDefaultLocalization("cs")

            val result = Localiser.localisePlural("sample__section__item_count", 2, "2")

            assertEquals("2 items-two", result)
        } finally {
            Locale.setDefault(previousLocale)
            Localiser.configureDefaultLocalization("en")
        }
    }

    @Test
    fun `fallback strings json accepts plural object with other only and falls back to other`() {
        val previousLocale = Locale.getDefault()

        try {
            Locale.setDefault(Locale.forLanguageTag("de"))
            Localiser.configureDefaultLocalization("cs")

            val result = Localiser.localisePlural("sample__section__fallback_count", 2, "2")

            assertEquals("2 items-other", result)
        } finally {
            Locale.setDefault(previousLocale)
            Localiser.configureDefaultLocalization("en")
        }
    }

    @Test
    fun `unsupported default localization language fails only on plural lookup`() {
        val previousLocale = Locale.getDefault()

        try {
            Locale.setDefault(Locale.forLanguageTag("de"))
            Localiser.configureDefaultLocalization("de")

            assertFailsWith<IllegalStateException> {
                Localiser.localisePlural("sample__section__item_count", 2, "2")
            }
        } finally {
            Locale.setDefault(previousLocale)
            Localiser.configureDefaultLocalization("en")
        }
    }

    @Test
    fun `malformed system localisation plural object fails during parsing`() {
        val previousLocale = Locale.getDefault()

        try {
            Locale.setDefault(Locale.forLanguageTag("it"))

            assertFailsWith<IllegalArgumentException> {
                Localiser.localisePlural("sample__section__broken_count", 1, "1")
            }

            assertFailsWith<IllegalArgumentException> {
                Localiser.localisePlural("sample__section__broken_count", 1, "1")
            }
        } finally {
            Locale.setDefault(previousLocale)
            Localiser.configureDefaultLocalization("en")
        }
    }
}
