package com.qinshift.linguine.linguinegenerator

import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.junit.jupiter.api.Test

@Suppress("StringLiteralDuplication")
class FileParserTest {

    @Test
    fun `generateNestedMapStructureFromJSON with empty input returns empty map`() {
        val mapContent: Map<String, String> = emptyMap()
        val fileParser = fileParser(fileContent = mapContent)

        val expectedOutput = mutableMapOf<String, Any>()

        val result = fileParser.generateNestedMapStructureFromJSON()

        result shouldBe expectedOutput
    }

    @Test
    fun `generateNestedMapStructureFromJSON with no delimiters in keys creates correct flat structure`() {
        val mapContent = mapOf(
            "singleKey" to "Single Value"
        )
        val fileParser = fileParser(fileContent = mapContent)

        val expectedOutput = mutableMapOf(
            "singleKey" to "singleKey"
        )

        val result = fileParser.generateNestedMapStructureFromJSON()

        result shouldBe expectedOutput
    }

    @Test
    fun `generateNestedMapStructureFromJSON with mixed case keys creates consistent camelCase output`() {
        val mapContent = mapOf(
            "activation__ForgottenPassword__emailInput" to "Enter your email"
        )
        val fileParser = fileParser(fileContent = mapContent)

        val expectedOutput = mutableMapOf(
            "Activation" to mutableMapOf(
                "ForgottenPassword" to mutableMapOf(
                    "emailInput" to "activation__ForgottenPassword__emailInput"
                )
            )
        )

        val result = fileParser.generateNestedMapStructureFromJSON()

        result shouldBe expectedOutput
    }

    @Test
    fun `generateNestedMapStructureFromJSON with extra delimiters creates deeply nested structure`() {
        val mapContent = mapOf(
            "activation____forgotten_password__email__input" to "Email Input"
        )
        val fileParser = fileParser(fileContent = mapContent)

        val expectedOutput = mutableMapOf(
            "Activation" to mutableMapOf(
                "" to mutableMapOf(
                    "ForgottenPassword" to mutableMapOf(
                        "Email" to mutableMapOf(
                            "input" to "activation____forgotten_password__email__input"
                        )
                    )
                )
            )
        )

        val result = fileParser.generateNestedMapStructureFromJSON()

        result shouldBe expectedOutput
    }

    @Test
    fun `generateNestedMapStructureFromJSON with valid input creates correct nested structure`() {
        val mapContent: Map<String, String> = mapOf(
            "activation__forgotten_password__birthdate__cancel_button" to "Cancel",
            "activation__forgotten_password__email_input" to "Enter your email",
            "home__welcome_message" to "Welcome to our application!",
            "profile__settings__privacy__title" to "Privacy Settings",
            "profile__settings__privacy__description" to "Manage your privacy settings here.",
            "checkout__payment__credit_card__number_input" to "Credit Card Number",
            "checkout__payment__credit_card__expiry_date" to "Expiry Date",
            "checkout__payment__credit_card__cvv" to "CVV"
        )
        val fileParser = fileParser(fileContent = mapContent)

        val expectedOutput = mutableMapOf(
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

        val result = fileParser.generateNestedMapStructureFromJSON()

        result shouldBe expectedOutput
    }

    private fun fileParser(
        fileContent: Map<String, String> = mockk(),
        minorDelimiter: String = "_",
        majorDelimiter: String = "__"
    ) = FileParser(
        fileContent = fileContent,
        minorDelimiter = minorDelimiter,
        majorDelimiter = majorDelimiter
    )
}
