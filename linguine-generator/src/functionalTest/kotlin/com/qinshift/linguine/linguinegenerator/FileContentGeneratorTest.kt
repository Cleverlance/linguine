package com.qinshift.linguine.linguinegenerator

import io.kotest.matchers.shouldBe
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("StringLiteralDuplication")
class FileContentGeneratorTest {

    @Test
    fun `generateFileContent with overlapping key names produces differentiated Kotlin object structures`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val fileContent: Map<String, String> = mapOf(
            "settings__privacy__title" to "Title for Privacy Settings",
            "settings__privacy" to "Privacy Settings",
            "settings__title" to "Title for Settings",
        )

        val root: Map<String, Any> = mapOf(
            "Privacy" to mapOf(
                "title" to ("settings__privacy__title" to "Title for Privacy Settings"),
            ),
            "privacy" to ("settings__privacy" to "Privacy Settings"),
            "title" to ("settings__title" to "Title for Settings"),
        )
        val generator = FileContentGenerator(sourceRoot, outputDirectory, fileContent)

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
        val fileContent: Map<String, String> = mapOf(
            "section__empty_value" to "",
        )

        val root: Map<String, Any> = mapOf(
            "emptyValue" to ("section__empty_value" to ""),
        )
        val generator = FileContentGenerator(sourceRoot, outputDirectory, fileContent)

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
        val fileContent: Map<String, String> = mapOf(
            "deep__level_one__level_two__level_three__final" to "Deeply Nested Value",
        )

        val root: Map<String, Any> = mapOf(
            "LevelOne" to mapOf(
                "LevelTwo" to mapOf(
                    "LevelThree" to mapOf(
                        "final" to ("deep__level_one__level_two__level_three__final" to "Deeply Nested Value"),
                    ),
                ),
            ),
        )
        val generator = FileContentGenerator(sourceRoot, outputDirectory, fileContent)

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
        val fileContent: Map<String, String> = mapOf(
            "simple__key" to "Simple Value",
            "another__simple__key" to "Another Simple Value",
        )

        val root: Map<String, Any> = mapOf(
            "Simple" to ("simple__key" to "Simple Value"),
            "AnotherSimple" to ("another__simple__key" to "Another Simple Value"),
        )
        val generator = FileContentGenerator(sourceRoot, outputDirectory, fileContent)

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
        val fileContent: Map<String, String> = mapOf(
            "error__message__with_parameters" to "Error %1\$s occurred at %2\$d:%3\$d on %4\$s",
        )

        val root: Map<String, Any> = mapOf(
            "Error" to mapOf(
                "messageWithParameters" to ("error__message__with_parameters" to
                    "Error %1\$s occurred at %2\$d:%3\$d on %4\$s"),
            ),
        )
        val generator = FileContentGenerator(sourceRoot, outputDirectory, fileContent)

        val result =
            generator.generateFileContent(outputDirectory.resolve("Strings.kt"), "Strings", root)

        val expected = """
        package com.example.app

        import com.qinshift.linguine.linguineruntime.presentation.Localiser.localise
        import kotlin.Int
        import kotlin.String

        public object Strings {
            public object Error {
                public fun messageWithParameters(
                    param1: String,
                    param2: Int,
                    param3: Int,
                    param4: String,
                ): String = localise("error__message__with_parameters", param1, param2, param3, param4)
            }
        }
    """
        assertEquals(expected.trimIndent(), result.trimIndent())
    }

    @Test
    fun `generateFileContent with special characters in keys produces expected Kotlin object structure`() {
        val sourceRoot = Path("src/main/kotlin")
        val outputDirectory = Path("src/main/kotlin/com/example/app/")
        val fileContent: Map<String, String> = mapOf(
            "special__char@cters__key!__value" to "Special Value",
            "another__special__key__with_numbers123" to "Numbered Value",
        )

        val root: Map<String, Any> = mapOf(
            "Special" to mapOf(
                "Characters" to mapOf(
                    "Key" to mapOf(
                        "value" to ("special__char@cters__key!__value" to "Special Value"),
                    ),
                ),
                "AnotherSpecial" to mapOf(
                    "keyWithNumbers123" to ("another__special__key__with_numbers123" to "Numbered Value"),
                ),
            ),
        )
        val generator = FileContentGenerator(sourceRoot, outputDirectory, fileContent)

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
        val fileContent: Map<String, String> = mapOf(
            "activation__forgotten_password__birthdate__cancel_button" to "Cancel",
            "activation__forgotten_password__email_input" to "Enter your email",
            "home__welcome_message" to "Welcome to our application!",
            "profile__settings__privacy__title" to "Privacy Settings",
            "profile__settings__privacy__description" to "Manage your privacy settings here.",
            "checkout__payment__credit_card__number_input" to "Credit Card Number",
            "checkout__payment__credit_card__expiry_date" to "Expiry Date",
            "checkout__payment__credit_card__cvv" to "CVV",
        )

        val root: Map<String, Any> = mapOf(
            "Activation" to mapOf(
                "ForgottenPassword" to mapOf(
                    "Birthdate" to mapOf(
                        "cancelButton" to ("activation__forgotten_password__birthdate__cancel_button" to "Cancel"),
                    ),
                    "emailInput" to ("activation__forgotten_password__email_input" to "Enter your email"),
                ),
            ),
            "Home" to mapOf(
                "welcomeMessage" to ("home__welcome_message" to "Welcome to our application!"),
            ),
            "Profile" to mapOf(
                "Settings" to mapOf(
                    "Privacy" to mapOf(
                        "title" to ("profile__settings__privacy__title" to "Privacy Settings"),
                        "description" to ("profile__settings__privacy__description" to
                            "Manage your privacy settings here."),
                    ),
                ),
            ),
            "Checkout" to mapOf(
                "Payment" to mapOf(
                    "CreditCard" to mapOf(
                        "numberInput" to ("checkout__payment__credit_card__number_input" to "Credit Card Number"),
                        "expiryDate" to ("checkout__payment__credit_card__expiry_date" to "Expiry Date"),
                        "cvv" to ("checkout__payment__credit_card__cvv" to "CVV"),
                    ),
                ),
            ),
        )
        val generator = FileContentGenerator(sourceRoot, outputDirectory, fileContent)

        val result =
            generator.generateFileContent(outputDirectory.resolve("Strings.kt"), "Strings", root)

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
        val fileContent: Map<String, String> = mapOf(
            "activation__forgotten_password__birthdate__cancel_button" to "\"%s %d %f %${'$'}s %${'$'}d %${'$'}f\"",
        )

        val root: Map<String, Any> = mapOf(
            "Activation" to mapOf(
                "ForgottenPassword" to mapOf(
                    "Birthdate" to mapOf(
                        "cancelButton" to ("activation__forgotten_password__birthdate__cancel_button" to
                            "\"%s %d %f %${'$'}s %${'$'}d %${'$'}f\""),
                    ),
                ),
            ),
        )
        val generator = FileContentGenerator(sourceRoot, outputDirectory, fileContent)

        val result =
            generator.generateFileContent(outputDirectory.resolve("Strings.kt"), "Strings", root)

        val expected = """
            package com.example.app
            
            import com.qinshift.linguine.linguineruntime.presentation.Localiser.localise
            import kotlin.Float
            import kotlin.Int
            import kotlin.String
            
            public object Strings {
                public object Activation {
                    public object ForgottenPassword {
                        public object Birthdate {
                            public fun cancelButton(
                                param1: String,
                                param2: Int,
                                param3: Float,
                                param4: String,
                                param5: Int,
                                param6: Float,
                            ): String = localise("activation__forgotten_password__birthdate__cancel_button",
                                    param1, param2, param3, param4, param5, param6)
                        }
                    }
                }
            }
        """
        result.trimIndent() shouldBe expected.trimIndent()
    }
}
