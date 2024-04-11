package com.qinshift.linguine.linguinegenerator

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class FileContentGeneratorTest {

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
            "checkout__payment__credit_card__cvv" to "CVV"
        )

        val root: MutableMap<String, Any> = mutableMapOf(
            "Activation" to mutableMapOf(
                "ForgottenPassword" to mutableMapOf(
                    "Birthdate" to mutableMapOf(
                        "cancelButton" to "activation__forgotten_password__birthdate__cancel_button"
                    ),
                    "emailInput" to "activation__forgotten_password__email_input"
                )
            ),
            "Home" to mutableMapOf(
                "welcomeMessage" to "home__welcome_message"
            ),
            "Profile" to mutableMapOf(
                "Settings" to mutableMapOf(
                    "Privacy" to mutableMapOf(
                        "title" to "profile__settings__privacy__title",
                        "description" to "profile__settings__privacy__description"
                    )
                )
            ),
            "Checkout" to mutableMapOf(
                "Payment" to mutableMapOf(
                    "CreditCard" to mutableMapOf(
                        "numberInput" to "checkout__payment__credit_card__number_input",
                        "expiryDate" to "checkout__payment__credit_card__expiry_date",
                        "cvv" to "checkout__payment__credit_card__cvv"
                    )
                )
            )
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
                        "cancelButton" to "activation__forgotten_password__birthdate__cancel_button"
                    )
                )
            )
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
