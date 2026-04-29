package com.qinshift.linguine.linguineruntime.presentation

import kotlin.test.Test
import kotlin.test.assertEquals

class LocalisationTest {

    @Test
    fun `text localisation formats single unindexed placeholder`() {
        val localisation = Localisation.MapBased(
            language = Language("en"),
            map = mapOf(
                "sample__section__label" to LocalisationValue.Text("Value %s"),
            ),
        )

        val result = localisation.get("sample__section__label", "Sample")

        assertEquals("Value Sample", result)
    }

    @Test
    fun `text localisation formats indexed placeholders without reprocessing inserted arguments`() {
        val localisation = Localisation.MapBased(
            language = Language("en"),
            map = mapOf(
                "sample__section__label" to LocalisationValue.Text("%1\$s and %2\$s"),
            ),
        )

        val result = localisation.get("sample__section__label", "%2\$s", "Sample")

        assertEquals("%2\$s and Sample", result)
    }

    @Test
    fun `text localisation formats indexed placeholders in reversed order`() {
        val localisation = Localisation.MapBased(
            language = Language("en"),
            map = mapOf(
                "sample__section__label" to LocalisationValue.Text("%2\$s and %1\$s"),
            ),
        )

        val result = localisation.get("sample__section__label", "First", "Second")

        assertEquals("Second and First", result)
    }

    @Test
    fun `plural localisation formats count and extra args`() {
        val localisation = Localisation.MapBased(
            language = Language("en"),
            map = mapOf(
                "sample__section__item_count" to LocalisationValue.Plural(
                    LocalisationForms(
                        mapOf(
                            "zero" to "%1\$d items-zero for %2\$s",
                            "one" to "%1\$d item for %2\$s",
                            "few" to "%1\$d items-few for %2\$s",
                            "many" to "%1\$d items-many for %2\$s",
                            "other" to "%1\$d items for %2\$s",
                        ),
                    ),
                ),
            ),
        )

        val result = localisation.getPlural("sample__section__item_count", 3, "3", "Sample User")

        assertEquals("3 items for Sample User", result)
    }
}
