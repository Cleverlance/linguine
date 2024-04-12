package com.qinshift.linguine.linguinegenerator

import io.kotest.matchers.shouldBe
import kotlin.test.Test

@Suppress("StringLiteralDuplication")
class FileContentGeneratorTest {

    @Test
    fun `generateFileContent with overlapping key names produces differentiated Kotlin object structures`() {
        val fileContent: Map<String, String> = mapOf(
            "settings__privacy__title" to "Title for Privacy Settings",
            "settings__privacy" to "Privacy Settings",
            "settings__title" to "Title for Settings"
        )

        val root: MutableMap<String, Any> = mutableMapOf(
            "Settings" to mutableMapOf(
                "privacy" to "settings__privacy",
                "title" to "settings__title",
                "Privacy" to mutableMapOf(
                    "title" to "settings__privacy__title"
                )
            )
        )
        val generator = FileContentGenerator(fileContent)

        val result = generator.generateFileContent(root).toString()

        val expected = """
public object Strings {
    public object Settings {
        public val privacy: String = localise("settings__privacy")
        public val title: String = localise("settings__title")
        public object Privacy {
            public val title: String = localise("settings__privacy__title")
        }
    }
}
"""
        result.trimIndent() shouldBe expected.trimIndent()
    }

    @Test
    fun `generateFileContent with empty values produces valid Kotlin object structures`() {
        val fileContent: Map<String, String> = mapOf(
            "section__empty_value" to ""
        )

        val root: MutableMap<String, Any> = mutableMapOf(
            "Section" to mutableMapOf(
                "emptyValue" to "section__empty_value"
            )
        )
        val generator = FileContentGenerator(fileContent)

        val result = generator.generateFileContent(root).toString()

        val expected = """
public object Strings {
    public object Section {
        public val emptyValue: String = localise("section__empty_value")
    }
}
"""
        result.trimIndent() shouldBe expected.trimIndent()
    }

    @Test
    fun `generateFileContent with deeply nested structures produces expected Kotlin object structure`() {
        val fileContent: Map<String, String> = mapOf(
            "deep__level_one__level_two__level_three__final" to "Deeply Nested Value"
        )

        val root: MutableMap<String, Any> = mutableMapOf(
            "Deep" to mutableMapOf(
                "LevelOne" to mutableMapOf(
                    "LevelTwo" to mutableMapOf(
                        "LevelThree" to mutableMapOf(
                            "final" to "deep__level_one__level_two__level_three__final"
                        )
                    )
                )
            )
        )
        val generator = FileContentGenerator(fileContent)

        val result = generator.generateFileContent(root).toString()

        val expected = """
public object Strings {
    public object Deep {
        public object LevelOne {
            public object LevelTwo {
                public object LevelThree {
                    public val final: String = localise("deep__level_one__level_two__level_three__final")
                }
            }
        }
    }
}
"""
        result.trimIndent() shouldBe expected.trimIndent()
    }

    @Test
    fun `generateFileContent with simple values produces expected Kotlin properties`() {
        val fileContent: Map<String, String> = mapOf(
            "simple__key" to "Simple Value",
            "another__simple__key" to "Another Simple Value",
        )

        val root: MutableMap<String, Any> = mutableMapOf(
            "Simple" to "simple__key",
            "AnotherSimple" to "another__simple__key",
        )
        val generator = FileContentGenerator(fileContent)

        val result = generator.generateFileContent(root).toString()

        val expected = """
public object Strings {
    public val Simple: String = localise("simple__key")
    public val AnotherSimple: String = localise("another__simple__key")
}
"""
        result.trimIndent() shouldBe expected.trimIndent()
    }

    @Test
    fun `generateFileContent with complex function parameterization generates correct function signatures`() {
        val fileContent: Map<String, String> = mapOf(
            "error__message__with_parameters" to "Error %1\$s occurred at %2\$d:%3\$d on %4\$s"
        )

        val root: MutableMap<String, Any> = mutableMapOf(
            "Error" to mutableMapOf(
                "messageWithParameters" to "error__message__with_parameters"
            )
        )
        val generator = FileContentGenerator(fileContent)

        val result = generator.generateFileContent(root).toString()

        val expected = """
public object Strings {
    public object Error {
        public fun messageWithParameters(param0: String, param1: Int, param2: Int, param3: String): String {
            return localise("error__message__with_parameters", param0, param1, param2, param3)
        }
    }
}
"""
        result.trimIndent() shouldBe expected.trimIndent()
    }

    @Test
    fun `generateFileContent with special characters in keys produces expected Kotlin object structure`() {
        val fileContent: Map<String, String> = mapOf(
            "special__char@cters__key!__value" to "Special Value",
            "another__special__key__with_numbers123" to "Numbered Value",
        )

        val root: MutableMap<String, Any> = mutableMapOf(
            "Special" to mutableMapOf(
                "Characters" to mutableMapOf(
                    "Key" to mutableMapOf(
                        "value" to "special__char@cters__key!__value",
                    ),
                ),
                "AnotherSpecial" to mutableMapOf(
                    "keyWithNumbers123" to "another__special__key__with_numbers123",
                ),
            ),
        )
        val generator = FileContentGenerator(fileContent)

        val result = generator.generateFileContent(root).toString()

        val expected = """
public object Strings {
    public object Special {
        public object Characters {
            public object Key {
                public val value: String = localise("special__char@cters__key!__value")
            }
        }
        public object AnotherSpecial {
            public val keyWithNumbers123: String = localise("another__special__key__with_numbers123")
        }
    }
}
"""
        result.trimIndent() shouldBe expected.trimIndent()
    }

    @Suppress("LongMethod")
    @Test
    fun `generateFileContent with simple map produces expected Kotlin object structure`() {
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

        val root: MutableMap<String, Any> = mutableMapOf(
            "Activation" to mutableMapOf(
                "ForgottenPassword" to mutableMapOf(
                    "Birthdate" to mutableMapOf(
                        "cancelButton" to "activation__forgotten_password__birthdate__cancel_button",
                    ),
                    "emailInput" to "activation__forgotten_password__email_input",
                ),
            ),
            "Home" to mutableMapOf(
                "welcomeMessage" to "home__welcome_message",
            ),
            "Profile" to mutableMapOf(
                "Settings" to mutableMapOf(
                    "Privacy" to mutableMapOf(
                        "title" to "profile__settings__privacy__title",
                        "description" to "profile__settings__privacy__description",
                    ),
                ),
            ),
            "Checkout" to mutableMapOf(
                "Payment" to mutableMapOf(
                    "CreditCard" to mutableMapOf(
                        "numberInput" to "checkout__payment__credit_card__number_input",
                        "expiryDate" to "checkout__payment__credit_card__expiry_date",
                        "cvv" to "checkout__payment__credit_card__cvv",
                    ),
                ),
            ),
        )
        val generator = FileContentGenerator(fileContent)

        val result = generator.generateFileContent(root).toString()

        val expected = """
import com.qinshift.linguine.linguineruntime.presentation.Localiser.localise

public object Strings {
    public object Activation {
        public object ForgottenPassword {
            public object Birthdate {
                public val cancelButton: String = localise("activation__forgotten_password__birthdate__cancel_button")
            }
            public val emailInput: String = localise("activation__forgotten_password__email_input")
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
                public val numberInput: String = localise("checkout__payment__credit_card__number_input")
                public val expiryDate: String = localise("checkout__payment__credit_card__expiry_date")
                public val cvv: String = localise("checkout__payment__credit_card__cvv")
            }
        }
    }
}
"""
        result.trimIndent() shouldBe expected.trimIndent()
    }

    @Test
    fun `generateFileContent with function parameters generates kotlin object with function parameters`() {
        val fileContent: Map<String, String> = mapOf(
            "activation__forgotten_password__birthdate__cancel_button" to "\"%s %d %f %${'$'}s %${'$'}d %${'$'}f\"",
        )

        val root: MutableMap<String, Any> = mutableMapOf(
            "Activation" to mutableMapOf(
                "ForgottenPassword" to mutableMapOf(
                    "Birthdate" to mutableMapOf(
                        "cancelButton" to "activation__forgotten_password__birthdate__cancel_button",
                    ),
                ),
            ),
        )
        val generator = FileContentGenerator(fileContent)

        val result = generator.generateFileContent(root).toString()

        val expected = """
public object Strings {
    public object Activation {
        public object ForgottenPassword {
            public object Birthdate {
                public fun cancelButton(param0: String, param1: Int, param2: Float, param3: String, param4: Int, param5: Float): String {
                    return localise("activation__forgotten_password__birthdate__cancel_button", param0, param1, param2, param3, param4, param5)
                }
            }
        }
    }
}
"""
        result.trimIndent() shouldBe expected.trimIndent()
    }
}
