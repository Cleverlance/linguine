package com.qinshift.linguine.linguinegenerator

import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.junit.jupiter.api.Test

@Suppress("StringLiteralDuplication")
class FileParserTest {

    @Test
    fun `generateNestedMapStructure with empty input returns empty map`() {
        val mapContent: Map<String, String> = emptyMap()
        val fileParser = fileParser(fileContent = mapContent)

        val expectedOutput = mutableMapOf<String, Any>()

        val result = fileParser.generateNestedMapStructure()

        result shouldBe expectedOutput
    }

    @Test
    fun `generateNestedMapStructure with no delimiters in keys creates correct flat structure`() {
        val mapContent = mapOf(
            "singleKey" to "Single Value"
        )
        val fileParser = fileParser(fileContent = mapContent)

        val expectedOutput = mutableMapOf(
            "singleKey" to "singleKey"
        )

        val result = fileParser.generateNestedMapStructure()

        result shouldBe expectedOutput
    }

    @Test
    fun `generateNestedMapStructure with mixed case keys creates consistent camelCase output`() {
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

        val result = fileParser.generateNestedMapStructure()

        result shouldBe expectedOutput
    }

    @Test
    fun `generateNestedMapStructure with extra delimiters creates deeply nested structure`() {
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

        val result = fileParser.generateNestedMapStructure()

        result shouldBe expectedOutput
    }

    @Test
    fun `generateNestedMapStructure with valid input creates correct nested structure`() {
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

        val result = fileParser.generateNestedMapStructure()

        result shouldBe expectedOutput
    }

    @Test
    fun `generateNestedMapStructure with repetitive key elements creates valid nested map`() {
        val mapContent: Map<String, String> = mapOf(
            "profile__settings__privacy__privacy_policy" to "Privacy Policy",
            "profile__settings__privacy__privacy_policy__details" to "Detailed description"
        )
        val fileParser = fileParser(fileContent = mapContent)

        val expectedOutput = mutableMapOf(
            "Profile" to mutableMapOf(
                "Settings" to mutableMapOf(
                    "Privacy" to mutableMapOf(
                        "PrivacyPolicy" to mutableMapOf(
                            "details" to "profile__settings__privacy__privacy_policy__details"
                        ),
                        "privacyPolicy" to "profile__settings__privacy__privacy_policy"
                    )
                )
            )
        )

        val result = fileParser.generateNestedMapStructure()

        result shouldBe expectedOutput
    }

    @Test
    fun `generateNestedMapStructure with deeply nested structures creates expected map`() {
        val mapContent: Map<String, String> = mapOf(
            "system__config__database__settings__max_connections" to "100",
            "system__config__database__settings__timeout" to "30"
        )
        val fileParser = fileParser(fileContent = mapContent)

        val expectedOutput = mutableMapOf(
            "System" to mutableMapOf(
                "Config" to mutableMapOf(
                    "Database" to mutableMapOf(
                        "Settings" to mutableMapOf(
                            "maxConnections" to "system__config__database__settings__max_connections",
                            "timeout" to "system__config__database__settings__timeout"
                        )
                    )
                )
            )
        )

        val result = fileParser.generateNestedMapStructure()

        result shouldBe expectedOutput
    }

    @Test
    fun `generateNestedMapStructure with non-standard characters in keys handles correctly`() {
        val mapContent: Map<String, String> = mapOf(
            "user__name__first name" to "John",
            "user__name__last-name" to "Doe"
        )
        val fileParser = fileParser(fileContent = mapContent)

        val expectedOutput = mutableMapOf(
            "User" to mutableMapOf(
                "Name" to mutableMapOf(
                    "first name" to "user__name__first name",
                    "last-name" to "user__name__last-name"
                )
            )
        )

        val result = fileParser.generateNestedMapStructure()

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
