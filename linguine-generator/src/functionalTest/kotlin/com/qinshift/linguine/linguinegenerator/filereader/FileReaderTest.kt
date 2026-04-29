package com.qinshift.linguine.linguinegenerator.filereader

import com.qinshift.linguine.linguinegenerator.PluralFormPolicy
import com.qinshift.linguine.linguinegenerator.Translation
import java.io.File
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir

class FileReaderTest {

    @TempDir
    lateinit var testProjectDir: File

    @Test
    fun `read should return a correct map when given a JSON file pat`() {
        val file = File(testProjectDir, "test.json").apply {
            writeText(
                """
                {
                    "test__file__input_value": "Input Value",
                    "another__file__description_value": "Description"
                }
                """.trimIndent(),
            )
        }

        val fileReader = fileReader()
        val result = fileReader.read(file, FileType.JSON)
        val expectedResult = mapOf(
            "test__file__input_value" to Translation.Text("Input Value"),
            "another__file__description_value" to Translation.Text("Description"),
        )

        assertEquals(expectedResult, result)
    }

    @Test
    @Suppress("StringLiteralDuplication")
    fun `read should parse plural object values`() {
        val file = File(testProjectDir, "plural-test.json").apply {
            writeText(
                """
                {
                    "sample__section__item_count": {
                        "zero": "%1${'$'}s items-zero",
                        "one": "%1${'$'}s item",
                        "two": "%1${'$'}s items-two",
                        "few": "%1${'$'}s items-few",
                        "many": "%1${'$'}s items-many",
                        "other": "%1${'$'}s items"
                    }
                }
                """.trimIndent(),
            )
        }

        val fileReader = fileReader()
        val result = fileReader.read(file, FileType.JSON)
        val expectedResult = mapOf(
            "sample__section__item_count" to Translation.Plural(
                mapOf(
                    "zero" to "%1\$s items-zero",
                    "one" to "%1\$s item",
                    "two" to "%1\$s items-two",
                    "few" to "%1\$s items-few",
                    "many" to "%1\$s items-many",
                    "other" to "%1\$s items",
                ),
            ),
        )

        assertEquals(expectedResult, result)
    }

    @Test
    fun `read should fail when plural object does not contain all required forms`() {
        val file = File(testProjectDir, "plural-missing-forms.json").apply {
            writeText(
                """
                {
                    "sample__section__item_count": {
                        "one": "%1${'$'}s item",
                        "other": "%1${'$'}s items"
                    }
                }
                """.trimIndent(),
            )
        }

        assertThrows<IllegalArgumentException> {
            fileReader().read(file, FileType.JSON)
        }
    }

    @Test
    fun `read should validate every plural form combination with all forms policy`() {
        pluralFormCombinations().forEach { forms ->
            val file = writePluralFile("all-forms-${forms.joinToString("-")}.json", forms)

            if (forms == REQUIRED_PLURAL_FORMS.toSet()) {
                val result = fileReader().read(file, FileType.JSON)
                assertEquals(
                    Translation.Plural(REQUIRED_PLURAL_FORMS.associateWith { "$it value" }),
                    result.getValue(PLURAL_KEY),
                    "Expected all-forms policy to accept only complete plural form set: $forms",
                )
            } else {
                assertThrows<IllegalArgumentException> {
                    fileReader().read(file, FileType.JSON)
                }
            }
        }
    }

    @Test
    fun `read should parse plural object with other only policy`() {
        val file = File(testProjectDir, "plural-other-only.json").apply {
            writeText(
                """
                {
                    "sample__section__item_count": {
                        "other": "%1${'$'}s items"
                    }
                }
                """.trimIndent(),
            )
        }

        val result = fileReader(PluralFormPolicy.REQUIRE_OTHER_ONLY).read(file, FileType.JSON)
        val expectedResult = mapOf(
            "sample__section__item_count" to Translation.Plural(
                mapOf(
                    "other" to "%1\$s items",
                ),
            ),
        )

        assertEquals(expectedResult, result)
    }

    @Test
    fun `read should fail when other only policy plural object does not contain other form`() {
        val file = File(testProjectDir, "plural-missing-other.json").apply {
            writeText(
                """
                {
                    "sample__section__item_count": {
                        "one": "%1${'$'}s item"
                    }
                }
                """.trimIndent(),
            )
        }

        assertThrows<IllegalArgumentException> {
            fileReader(PluralFormPolicy.REQUIRE_OTHER_ONLY).read(file, FileType.JSON)
        }
    }

    @Test
    fun `read should validate every plural form combination with other only policy`() {
        pluralFormCombinations().forEach { forms ->
            val file = writePluralFile("other-only-${forms.joinToString("-")}.json", forms)

            if ("other" in forms) {
                val result = fileReader(PluralFormPolicy.REQUIRE_OTHER_ONLY).read(file, FileType.JSON)
                assertEquals(
                    Translation.Plural(forms.associateWith { "$it value" }),
                    result.getValue(PLURAL_KEY),
                    "Expected other-only policy to accept plural form set with other: $forms",
                )
            } else {
                assertThrows<IllegalArgumentException> {
                    fileReader(PluralFormPolicy.REQUIRE_OTHER_ONLY).read(file, FileType.JSON)
                }
            }
        }
    }

    @Test
    fun `read should fail when plural form value is not string`() {
        val file = File(testProjectDir, "plural-non-string-form.json").apply {
            writeText(
                """
                {
                    "$PLURAL_KEY": {
                        "zero": "zero value",
                        "one": "one value",
                        "two": "two value",
                        "few": "few value",
                        "many": "many value",
                        "other": 1
                    }
                }
                """.trimIndent(),
            )
        }

        assertThrows<IllegalArgumentException> {
            fileReader().read(file, FileType.JSON)
        }
    }

    @Test
    fun `read should fail when localisation value is neither string nor object`() {
        val file = File(testProjectDir, "unsupported-value.json").apply {
            writeText(
                """
                {
                    "sample__section__value": ["unexpected"]
                }
                """.trimIndent(),
            )
        }

        assertThrows<IllegalStateException> {
            fileReader().read(file, FileType.JSON)
        }
    }

    private fun writePluralFile(fileName: String, forms: Set<String>): File {
        return File(testProjectDir, fileName.ifBlank { "empty.json" }).apply {
            val formsContent = forms.joinToString(",\n") { form ->
                "\"$form\": \"$form value\""
            }
            writeText(
                """
                {
                    "$PLURAL_KEY": {
                        $formsContent
                    }
                }
                """.trimIndent(),
            )
        }
    }

    private fun pluralFormCombinations(): List<Set<String>> {
        return (0 until (1 shl REQUIRED_PLURAL_FORMS.size)).map { mask ->
            REQUIRED_PLURAL_FORMS
                .filterIndexed { index, _ -> mask and (1 shl index) != 0 }
                .toSet()
        }
    }

    private fun fileReader(
        pluralFormPolicy: PluralFormPolicy = PluralFormPolicy.REQUIRE_ALL_FORMS,
    ) = FileReader(
        pluralFormPolicy = pluralFormPolicy,
    )

    private companion object {
        const val PLURAL_KEY = "sample__section__item_count"
        val REQUIRED_PLURAL_FORMS = listOf("zero", "one", "two", "few", "many", "other")
    }
}
