package com.qinshift.linguine.linguinegenerator

import io.kotest.matchers.shouldBe
import java.nio.file.Path
import java.util.Locale
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("StringLiteralDuplication", "LargeClass")
internal class FileContentGeneratorTest {

    @Test
    fun `generateFileContent with overlapping key names produces differentiated Kotlin object structures`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val root = group(
            "Privacy" to group(
                "title" to leaf("settings__privacy__title", "Title for Privacy Settings"),
            ),
            "privacy" to leaf("settings__privacy", "Privacy Settings"),
            "title" to leaf("settings__title", "Title for Settings"),
        )
        val generator = FileContentGenerator(
            sourceRoot = sourceRoot,
            outputDirectory = outputDirectory,
            outputSuffix = "Strings",
        )

        val result = generator.generateFileContent(
            outputDirectory.resolve("SettingsStrings.kt"),
            "Settings",
            root,
        )

        val expected = """
            package com.example.app

            import com.qinshift.linguine.linguineruntime.presentation.Localiser.localise
            import kotlin.String

            public object Settings {
                public val privacy: String = localise("settings__privacy")

                public val title: String = localise("settings__title")

                public object Privacy {
                    public val title: String = localise("settings__privacy__title")
                }
            }
        """
        assertEquals(expected.trimIndent(), result.trimIndent())
    }

    @Test
    fun `generateFileContent with empty values produces valid Kotlin object structures`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val root = group(
            "emptyValue" to leaf("section__empty_value", ""),
        )
        val generator = FileContentGenerator(
            sourceRoot = sourceRoot,
            outputDirectory = outputDirectory,
            outputSuffix = "Strings",
        )

        val result = generator.generateFileContent(
            outputDirectory.resolve("SectionStrings.kt"),
            "Section",
            root,
        )

        val expected = """
        package com.example.app

        import com.qinshift.linguine.linguineruntime.presentation.Localiser.localise
        import kotlin.String

        public object Section {
            public val emptyValue: String = localise("section__empty_value")
        }
    """
        assertEquals(expected.trimIndent(), result.trimIndent())
    }

    @Test
    fun `generateFileContent with deeply nested structures produces expected Kotlin object structure`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val root = group(
            "LevelOne" to group(
                "LevelTwo" to group(
                    "LevelThree" to group(
                        "final" to leaf("deep__level_one__level_two__level_three__final", "Deeply Nested Value"),
                    ),
                ),
            ),
        )
        val generator = FileContentGenerator(
            sourceRoot = sourceRoot,
            outputDirectory = outputDirectory,
            outputSuffix = "Strings",
        )

        val result =
            generator.generateFileContent(outputDirectory.resolve("DeepStrings.kt"), "Deep", root)

        val expected = """
        package com.example.app

        import com.qinshift.linguine.linguineruntime.presentation.Localiser.localise
        import kotlin.String

        public object Deep {
            public object LevelOne {
                public object LevelTwo {
                    public object LevelThree {
                        public val `final`: String =
                                localise("deep__level_one__level_two__level_three__final")
                    }
                }
            }
        }
    """
        assertEquals(expected.trimIndent(), result.trimIndent())
    }

    @Test
    fun `generateFileContent with simple values produces expected Kotlin properties`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val root = group(
            "Simple" to leaf("simple__key", "Simple Value"),
            "AnotherSimple" to leaf("another__simple__key", "Another Simple Value"),
        )
        val generator = FileContentGenerator(
            sourceRoot = sourceRoot,
            outputDirectory = outputDirectory,
            outputSuffix = "Strings",
        )

        val result =
            generator.generateFileContent(outputDirectory.resolve("Strings.kt"), "Strings", root)

        val expected = """
        package com.example.app

        import com.qinshift.linguine.linguineruntime.presentation.Localiser.localise
        import kotlin.String

        public object Strings {
            public val Simple: String = localise("simple__key")

            public val AnotherSimple: String = localise("another__simple__key")
        }
    """
        assertEquals(expected.trimIndent(), result.trimIndent())
    }

    @Test
    fun `generateFileContent with complex function parameterization generates correct function signatures`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val root = group(
            "Error" to group(
                "messageWithParameters" to leaf(
                    "error__message__with_parameters",
                    "Error %1\$s occurred at %2\$d:%3\$d on %4\$s",
                ),
            ),
        )
        val generator = FileContentGenerator(
            sourceRoot = sourceRoot,
            outputDirectory = outputDirectory,
            outputSuffix = "Strings",
        )

        val result =
            generator.generateFileContent(outputDirectory.resolve("Strings.kt"), "Strings", root)

        val expected = """
        package com.example.app

        import com.qinshift.linguine.linguineruntime.presentation.Localiser.localise
        import kotlin.String

        public object Strings {
            public object Error {
                public fun messageWithParameters(
                    param1: String,
                    param2: String,
                    param3: String,
                    param4: String,
                ): String = localise("error__message__with_parameters", param1, param2, param3, param4)
            }
        }
    """
        assertEquals(expected.trimIndent(), result.trimIndent())
    }

    @Test
    fun `generateFileContent allows single unindexed placeholder`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val root = group(
            "label" to leaf(
                "sample__label",
                "Value %s",
            ),
        )

        val generator = FileContentGenerator(
            sourceRoot = sourceRoot,
            outputDirectory = outputDirectory,
            outputSuffix = "Strings",
        )

        val result =
            generator.generateFileContent(outputDirectory.resolve("SampleStrings.kt"), "SampleStrings", root)

        assertTrue(result.contains("public fun label(param1: String): String"))
    }

    @Test
    fun `generateFileContent fails when multiple placeholders contain unindexed placeholder`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val root = group(
            "label" to leaf(
                "sample__label",
                "%1\$s and %s",
            ),
        )

        val generator = FileContentGenerator(
            sourceRoot = sourceRoot,
            outputDirectory = outputDirectory,
            outputSuffix = "Strings",
        )

        assertFailsWith<IllegalArgumentException> {
            val path = outputDirectory.resolve("SampleStrings.kt")
            generator.generateFileContent(path, "SampleStrings", root)
        }
    }

    @Test
    fun `generateFileContent fails when placeholder has dollar without index`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val root = group(
            "label" to leaf(
                "sample__label",
                "Value %\$s",
            ),
        )

        val generator = FileContentGenerator(
            sourceRoot = sourceRoot,
            outputDirectory = outputDirectory,
            outputSuffix = "Strings",
        )

        assertFailsWith<IllegalArgumentException> {
            val path = outputDirectory.resolve("SampleStrings.kt")
            generator.generateFileContent(path, "SampleStrings", root)
        }
    }

    @Test
    fun `generateFileContent allows indexed placeholders in translated order`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val root = group(
            "label" to leaf(
                "sample__label",
                "%2\$s after %1\$s",
            ),
        )

        val generator = FileContentGenerator(
            sourceRoot = sourceRoot,
            outputDirectory = outputDirectory,
            outputSuffix = "Strings",
        )

        val result = generator.generateFileContent(
            outputDirectory.resolve("SampleStrings.kt"),
            "SampleStrings",
            root,
        )

        assertTrue(result.contains("public fun label("))
        assertTrue(result.contains("param1: String"))
        assertTrue(result.contains("param2: String"))
        assertTrue(Regex("""localise\("sample__label",\s*param1,\s*param2\)""").containsMatchIn(result))
    }

    @Test
    fun `generateFileContent fails when indexed placeholders skip a parameter`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val root = group(
            "label" to leaf(
                "sample__label",
                "%1\$s and %3\$s",
            ),
        )

        val generator = FileContentGenerator(
            sourceRoot = sourceRoot,
            outputDirectory = outputDirectory,
            outputSuffix = "Strings",
        )

        assertFailsWith<IllegalArgumentException> {
            generator.generateFileContent(
                outputDirectory.resolve("SampleStrings.kt"),
                "SampleStrings",
                root,
            )
        }
    }

    @Test
    fun `generateFileContent fails when indexed placeholder sequence starts after one`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val root = group(
            "label" to leaf(
                "sample__label",
                "%2\$s",
            ),
        )

        val generator = FileContentGenerator(
            sourceRoot = sourceRoot,
            outputDirectory = outputDirectory,
            outputSuffix = "Strings",
        )

        assertFailsWith<IllegalArgumentException> {
            generator.generateFileContent(
                outputDirectory.resolve("SampleStrings.kt"),
                "SampleStrings",
                root,
            )
        }
    }

    @Test
    fun `generateFileContent with plural value generates plural accessor`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val pluralEntry = Translation.Plural(
            mapOf(
                "zero" to "%1\$s items-zero",
                "one" to "%1\$s item",
                "few" to "%1\$s items-few",
                "many" to "%1\$s items-many",
                "other" to "%1\$s items",
            ),
        )

        val root = group(
            "Section" to group(
                "itemCount" to pluralLeaf(
                    "sample__section__item_count",
                    pluralEntry,
                ),
            ),
        )

        val generator = FileContentGenerator(
            sourceRoot = sourceRoot,
            outputDirectory = outputDirectory,
            outputSuffix = "Strings",
        )

        val path = outputDirectory.resolve("SampleStrings.kt")
        val result = generator.generateFileContent(path, "SampleStrings", root)

        val expected = """
        package com.example.app

        import com.qinshift.linguine.linguineruntime.presentation.Localiser.localise
        import com.qinshift.linguine.linguineruntime.presentation.Localiser.localisePlural
        import kotlin.Number
        import kotlin.String

        public object SampleStrings {
            public object Section {
                public fun itemCount(count: Number, param1: String): String =
                        localisePlural("sample__section__item_count", count, param1)
            }
        }
    """

        assertEquals(expected.trimIndent(), result.trimIndent())
    }

    @Test
    fun `generateFileContent creates one plural parameter for repeated single unindexed placeholders`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val root = group(
            "Section" to group(
                "itemCount" to pluralLeaf(
                    "sample__section__item_count",
                    Translation.Plural(
                        mapOf(
                            "zero" to "%s items-zero",
                            "one" to "%s item",
                            "few" to "%s items-few",
                            "many" to "%s items-many",
                            "other" to "%s items",
                        ),
                    ),
                ),
            ),
        )

        val generator = FileContentGenerator(
            sourceRoot = sourceRoot,
            outputDirectory = outputDirectory,
            outputSuffix = "Strings",
        )

        val result = generator.generateFileContent(
            outputDirectory.resolve("SampleStrings.kt"),
            "SampleStrings",
            root,
        )

        assertTrue(result.contains("public fun itemCount(count: Number, param1: String): String"))
        assertFalse(result.contains("param2: String"))
    }

    @Test
    fun `generateFileContent with plural value without placeholders generates count-only plural accessor`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val root = group(
            "Section" to group(
                "itemCount" to pluralLeaf(
                    "sample__section__item_count",
                    Translation.Plural(
                        mapOf(
                            "zero" to "No items",
                            "one" to "One item",
                            "few" to "Few items",
                            "many" to "Many items",
                            "other" to "Items",
                        ),
                    ),
                ),
            ),
        )

        val generator = FileContentGenerator(
            sourceRoot = sourceRoot,
            outputDirectory = outputDirectory,
            outputSuffix = "Strings",
        )

        val path = outputDirectory.resolve("SampleStrings.kt")
        val result = generator.generateFileContent(path, "SampleStrings", root)

        assertTrue(result.contains("public fun itemCount(count: Number): String"))
        assertTrue(result.contains("""localisePlural("sample__section__item_count","""))
        assertTrue(result.contains("count)"))
        assertFalse(result.contains("count, )"))
    }

    @Test
    fun `generateFileContent creates localise function with parameter count and types matching placeholders`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val root = group(
            "Section" to group(
                "formattedLabel" to leaf(
                    "sample__section__formatted_label",
                    "Value %1\$s / %2\$d / %3\$f",
                ),
            ),
        )

        val generator = FileContentGenerator(
            sourceRoot = sourceRoot,
            outputDirectory = outputDirectory,
            outputSuffix = "Strings",
        )

        val path = outputDirectory.resolve("SampleStrings.kt")
        val result = generator.generateFileContent(path, "SampleStrings", root)

        assertTrue(result.contains("public fun formattedLabel("))
        assertTrue(result.contains("param1: String"))
        assertTrue(result.contains("param2: String"))
        assertTrue(result.contains("param3: String"))
    }

    @Test
    fun `generateFileContent creates localisePlural function with parameter count and types matching placeholders`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val root = group(
            "Section" to group(
                "formattedCount" to pluralLeaf(
                    "sample__section__formatted_count",
                    Translation.Plural(
                        mapOf(
                            "zero" to "%1\$s / %2\$d / %3\$f",
                            "one" to "%1\$s",
                            "few" to "%1\$s / %2\$d / %3\$f",
                            "many" to "%1\$s / %2\$d / %3\$f",
                            "other" to "%1\$s / %2\$d / %3\$f",
                        ),
                    ),
                ),
            ),
        )

        val generator = FileContentGenerator(
            sourceRoot = sourceRoot,
            outputDirectory = outputDirectory,
            outputSuffix = "Strings",
        )

        val path = outputDirectory.resolve("SampleStrings.kt")
        val result = generator.generateFileContent(path, "SampleStrings", root)

        assertTrue(result.contains("public fun formattedCount("))
        assertTrue(result.contains("count: Number"))
        assertTrue(result.contains("param1: String"))
        assertTrue(result.contains("param2: String"))
        assertTrue(result.contains("param3: String"))
    }

    @Test
    fun `generateFileContent creates plural signature from different indexed subsets when union is sequential`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val root = group(
            "Section" to group(
                "scopeValue" to pluralLeaf(
                    "sample__section__scope_value",
                    Translation.Plural(
                        mapOf(
                            "zero" to "%1\$s zero",
                            "one" to "%1\$s one",
                            "few" to "%1\$s few %3\$s",
                            "many" to "N/A %2\$s",
                            "other" to "%1\$s other %2\$s %3\$s",
                        ),
                    ),
                ),
            ),
        )

        val generator = FileContentGenerator(
            sourceRoot = sourceRoot,
            outputDirectory = outputDirectory,
            outputSuffix = "Strings",
        )

        val result = generator.generateFileContent(
            outputDirectory.resolve("SampleStrings.kt"),
            "SampleStrings",
            root,
        )

        assertTrue(result.contains("public fun scopeValue("))
        assertTrue(result.contains("count: Number"))
        assertTrue(result.contains("param1: String"))
        assertTrue(result.contains("param2: String"))
        assertTrue(result.contains("param3: String"))
        assertFalse(result.contains("param4: String"))
    }

    @Test
    fun `generateFileContent fails when plural indexed placeholder union is not sequential`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val root = group(
            "Section" to group(
                "scopeValue" to pluralLeaf(
                    "sample__section__scope_value",
                    Translation.Plural(
                        mapOf(
                            "zero" to "%1\$s zero",
                            "one" to "%1\$s one",
                            "few" to "%1\$s few",
                            "many" to "%3\$s many",
                            "other" to "%1\$s other",
                        ),
                    ),
                ),
            ),
        )

        val generator = FileContentGenerator(
            sourceRoot = sourceRoot,
            outputDirectory = outputDirectory,
            outputSuffix = "Strings",
        )

        assertFailsWith<IllegalArgumentException> {
            generator.generateFileContent(
                outputDirectory.resolve("SampleStrings.kt"),
                "SampleStrings",
                root,
            )
        }
    }

    @Test
    fun `generateFileContent creates plural signature from placeholders across all forms`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val root = group(
            "Section" to group(
                "scopeValue" to pluralLeaf(
                    "sample__section__scope_value",
                    Translation.Plural(
                        mapOf(
                            "zero" to "%1\$s zero %4\$s",
                            "one" to "%1\$s one",
                            "few" to "%1\$s few %2\$s",
                            "many" to "%1\$s many %3\$s",
                            "other" to "%1\$s other",
                        ),
                    ),
                ),
            ),
        )

        val generator = FileContentGenerator(
            sourceRoot = sourceRoot,
            outputDirectory = outputDirectory,
            outputSuffix = "Strings",
        )

        val path = outputDirectory.resolve("SampleStrings.kt")
        val result = generator.generateFileContent(path, "SampleStrings", root)

        assertTrue(result.contains("public fun scopeValue("))
        assertTrue(result.contains("count: Number"))
        assertTrue(result.contains("param1: String"))
        assertTrue(result.contains("param2: String"))
        assertTrue(result.contains("param3: String"))
        assertTrue(result.contains("param4: String"))
        assertTrue(Regex("""param1,\s*param2,\s*param3,\s*param4""").containsMatchIn(result))
    }

    @Test
    fun `generateFileContent creates plural signature with all indexed placeholder parameters`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val root = group(
            "Section" to group(
                "scopeValue" to pluralLeaf(
                    "sample__section__scope_value",
                    Translation.Plural(
                        mapOf(
                            "zero" to "%1\$d zero %2\$s",
                            "one" to "%1\$d singular %2\$s",
                            "few" to "%1\$d few %2\$s",
                            "many" to "N/A %2\$s",
                            "other" to "%1\$d other %2\$s",
                        ),
                    ),
                ),
            ),
        )

        val generator = FileContentGenerator(
            sourceRoot = sourceRoot,
            outputDirectory = outputDirectory,
            outputSuffix = "Strings",
        )

        val path = outputDirectory.resolve("SampleStrings.kt")
        val result = generator.generateFileContent(path, "SampleStrings", root)

        assertTrue(result.contains("public fun scopeValue("))
        assertTrue(result.contains("count: Number"))
        assertTrue(result.contains("param1: String"))
        assertTrue(result.contains("param2: String"))
    }

    @Test
    fun `generateFileContent with special characters in keys produces expected Kotlin object structure`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val root = group(
            "Special" to group(
                "Characters" to group(
                    "Key" to group(
                        "value" to leaf(
                            "special__char@cters__key!__value",
                            "Special Value",
                        ),
                    ),
                ),
                "AnotherSpecial" to group(
                    "keyWithNumbers123" to leaf(
                        "another__special__key__with_numbers123",
                        "Numbered Value",
                    ),
                ),
            ),
        )
        val generator = FileContentGenerator(
            sourceRoot = sourceRoot,
            outputDirectory = outputDirectory,
            outputSuffix = "Strings",
        )

        val result =
            generator.generateFileContent(outputDirectory.resolve("Strings.kt"), "Strings", root)

        val expected = """
        package com.example.app

        import com.qinshift.linguine.linguineruntime.presentation.Localiser.localise
        import kotlin.String

        public object Strings {
            public object Special {
                public object Characters {
                    public object Key {
                        public val `value`: String = localise("special__char@cters__key!__value")
                    }
                }

                public object AnotherSpecial {
                    public val keyWithNumbers123: String =
                            localise("another__special__key__with_numbers123")
                }
            }
        }
    """
        assertEquals(expected.trimIndent(), result.trimIndent())
    }

    @Suppress("LongMethod")
    @Test
    fun `generateFileContent with simple map produces expected Kotlin object structure`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val root = group(
            "Activation" to group(
                "ForgottenPassword" to group(
                    "Birthdate" to group(
                        "cancelButton" to leaf(
                            "activation__forgotten_password__birthdate__cancel_button",
                            "Cancel",
                        ),
                    ),
                    "emailInput" to leaf(
                        "activation__forgotten_password__email_input",
                        "Enter your email",
                    ),
                ),
            ),
            "Home" to group(
                "welcomeMessage" to leaf(
                    "home__welcome_message",
                    "Welcome to our application!",
                ),
            ),
            "Profile" to group(
                "Settings" to group(
                    "Privacy" to group(
                        "title" to leaf(
                            "profile__settings__privacy__title",
                            "Privacy Settings",
                        ),
                        "description" to leaf(
                            "profile__settings__privacy__description",
                            "Manage your privacy settings here.",
                        ),
                    ),
                ),
            ),
            "Checkout" to group(
                "Payment" to group(
                    "CreditCard" to group(
                        "numberInput" to leaf(
                            "checkout__payment__credit_card__number_input",
                            "Credit Card Number",
                        ),
                        "expiryDate" to leaf(
                            "checkout__payment__credit_card__expiry_date",
                            "Expiry Date",
                        ),
                        "cvv" to leaf(
                            "checkout__payment__credit_card__cvv",
                            "CVV",
                        ),
                    ),
                ),
            ),
        )
        val generator = FileContentGenerator(
            sourceRoot = sourceRoot,
            outputDirectory = outputDirectory,
            outputSuffix = "Strings",
        )

        val path = outputDirectory.resolve("Strings.kt")
        val result = generator.generateFileContent(path, "Strings", root)

        val expected = """
        package com.example.app

        import com.qinshift.linguine.linguineruntime.presentation.Localiser.localise
        import kotlin.String

        public object Strings {
            public object Activation {
                public object ForgottenPassword {
                    public val emailInput: String = localise("activation__forgotten_password__email_input")

                    public object Birthdate {
                        public val cancelButton: String =
                                localise("activation__forgotten_password__birthdate__cancel_button")
                    }
                }
            }

            public object Home {
                public val welcomeMessage: String = localise("home__welcome_message")
            }

            public object Profile {
                public object Settings {
                    public object Privacy {
                        public val title: String = localise("profile__settings__privacy__title")

                        public val description: String = localise("profile__settings__privacy__description")
                    }
                }
            }

            public object Checkout {
                public object Payment {
                    public object CreditCard {
                        public val numberInput: String =
                                localise("checkout__payment__credit_card__number_input")

                        public val expiryDate: String =
                                localise("checkout__payment__credit_card__expiry_date")

                        public val cvv: String = localise("checkout__payment__credit_card__cvv")
                    }
                }
            }
        }
    """
        assertEquals(expected.trimIndent(), result.trimIndent())
    }

    @Test
    fun `generateFileContent with function parameters generates kotlin object with function parameters`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val root = group(
            "Activation" to group(
                "ForgottenPassword" to group(
                    "Birthdate" to group(
                        "cancelButton" to leaf(
                            "activation__forgotten_password__birthdate__cancel_button",
                            "\"%1${'$'}s %2${'$'}d %3${'$'}f %4${'$'}s %5${'$'}d %6${'$'}f\"",
                        ),
                    ),
                ),
            ),
        )
        val generator = FileContentGenerator(
            sourceRoot = sourceRoot,
            outputDirectory = outputDirectory,
            outputSuffix = "Strings",
        )

        val path = outputDirectory.resolve("Strings.kt")
        val result = generator.generateFileContent(path, "Strings", root)

        val expected = """
            package com.example.app
            
            import com.qinshift.linguine.linguineruntime.presentation.Localiser.localise
            import kotlin.String
            
            public object Strings {
                public object Activation {
                    public object ForgottenPassword {
                        public object Birthdate {
                            public fun cancelButton(
                                param1: String,
                                param2: String,
                                param3: String,
                                param4: String,
                                param5: String,
                                param6: String,
                            ): String = localise("activation__forgotten_password__birthdate__cancel_button",
                                    param1, param2, param3, param4, param5, param6)
                        }
                    }
                }
            }
        """
        result.trimIndent() shouldBe expected.trimIndent()
    }

    @Test
    fun `generateFileContents uses outputSuffix in file name and root object name`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val groupedMap = mapOf(
            "Home" to group(
                "title" to leaf("home__title", "Home Title"),
            ),
        )

        val generator = FileContentGenerator(
            sourceRoot = sourceRoot,
            outputDirectory = outputDirectory,
            outputSuffix = "L10n",
        )

        val result = generator.generateFileContents(groupedMap)

        result.size shouldBe 1

        val (path, content) = result.entries.single()

        path shouldBe outputDirectory.resolve("HomeL10n.kt")

        assertTrue(content.contains("public object HomeL10n"))

        assertTrue(content.contains("""public val title: String = localise("home__title")"""))
    }

    @Test
    fun `generateFileContents capitalizes lowercase group name and applies suffix`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val groupedMap = mapOf(
            "home" to group(
                "title" to leaf("home__title", "Home Title"),
            ),
        )

        val generator = FileContentGenerator(
            sourceRoot = sourceRoot,
            outputDirectory = outputDirectory,
            outputSuffix = "L10n",
        )

        val result = generator.generateFileContents(groupedMap)

        result.size shouldBe 1

        val (path, content) = result.entries.single()

        path shouldBe outputDirectory.resolve("HomeL10n.kt")

        assertTrue(content.contains("public object HomeL10n"))

        assertTrue(content.contains("""public val title: String = localise("home__title")"""))
    }

    @Test
    fun `generateFileContents capitalizes identifiers independent from default locale`() {
        val previousLocale = Locale.getDefault()

        try {
            Locale.setDefault(Locale.forLanguageTag("tr"))

            val sourceRoot = Path("src/main/kotlin")
            val outputDirectory = Path("src/main/kotlin/com/example/app/")
            val groupedMap = mapOf(
                "item" to group(
                    "inner" to group(
                        "title" to leaf("item__inner__title", "Item Title"),
                    ),
                ),
            )

            val generator = FileContentGenerator(
                sourceRoot = sourceRoot,
                outputDirectory = outputDirectory,
                outputSuffix = "L10n",
            )

            val result = generator.generateFileContents(groupedMap)
            val (path, content) = result.entries.single()

            path shouldBe outputDirectory.resolve("ItemL10n.kt")
            assertTrue(content.contains("public object ItemL10n"))
            assertTrue(content.contains("public object Inner"))
            assertFalse(content.contains("İ"))
        } finally {
            Locale.setDefault(previousLocale)
        }
    }

    @Test
    fun `generateFileContent falls back to presentation package when relative path is blank`() {
        val sourceRoot = Path("src/main/kotlin/com/example/app")
        val outputDirectory = Path("src/main/kotlin/com/example/app")
        val root = group(
            "key" to leaf("key", "Value"),
        )

        val generator = FileContentGenerator(
            sourceRoot = sourceRoot,
            outputDirectory = outputDirectory,
            outputSuffix = "Strings",
        )

        val result = generator.generateFileContent(
            outputDirectory.resolve("Strings.kt"),
            "Strings",
            root,
        )

        assertTrue(result.trimStart().startsWith("package presentation"))
    }

    @Test
    fun `generateFileContent falls back to property when translation key is missing`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val root = group(
            "title" to leaf("missing_key", "This value is ignored by generator"),
        )

        val generator = FileContentGenerator(
            sourceRoot = sourceRoot,
            outputDirectory = outputDirectory,
            outputSuffix = "Strings",
        )

        val result =
            generator.generateFileContent(outputDirectory.resolve("Strings.kt"), "Strings", root)

        assertTrue(result.contains("""public val title: String = localise("missing_key")"""))
    }

    private fun group(vararg children: Pair<String, Node>): Node.Group =
        Node.Group(children.toMap())

    private fun leaf(originalKey: String, value: String): Node.Item =
        Node.Item(
            key = originalKey,
            translation = Translation.Text(value),
        )

    private fun pluralLeaf(originalKey: String, value: Translation.Plural): Node.Item =
        Node.Item(
            key = originalKey,
            translation = value,
        )
}
