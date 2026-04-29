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

        val expectedOutput = emptyMap<String, Node.Group>()

        val result = fileParser.generateGroupedNodeStructure()

        result shouldBe expectedOutput
    }

    @Test
    fun `generateNestedMapStructure with no delimiters in keys creates correct flat structure`() {
        val mapContent = mapOf(
            "singleKey" to "Single Value",
        )
        val fileParser = fileParser(fileContent = mapContent)

        val expectedOutput = mapOf(
            "SingleKey" to nodeGroup(
                "singleKey" to nodeItem("singleKey", "Single Value"),
            ),
        )

        val result = fileParser.generateGroupedNodeStructure()

        result shouldBe expectedOutput
    }

    @Test
    fun `generateNestedMapStructure with mixed case keys creates consistent camelCase output`() {
        val mapContent = mapOf(
            "activation__forgottenPassword__emailInput" to "Enter your email",
        )
        val fileParser = fileParser(fileContent = mapContent)

        val expectedOutput = mapOf(
            "Activation" to nodeGroup(
                "ForgottenPassword" to nodeGroup(
                    "emailInput" to nodeItem(
                        "activation__forgottenPassword__emailInput",
                        "Enter your email",
                    ),
                ),
            ),
        )

        val result = fileParser.generateGroupedNodeStructure()

        result shouldBe expectedOutput
    }

    @Test
    fun `generateNestedMapStructure with extra delimiters creates deeply nested structure`() {
        val mapContent = mapOf(
            "activation____forgotten_password__email__input" to "Email Input",
        )
        val fileParser = fileParser(fileContent = mapContent)

        val expectedOutput = mapOf(
            "Activation" to nodeGroup(
                "" to nodeGroup(
                    "ForgottenPassword" to nodeGroup(
                        "Email" to nodeGroup(
                            "input" to nodeItem(
                                "activation____forgotten_password__email__input",
                                "Email Input",
                            ),
                        ),
                    ),
                ),
            ),
        )

        val result = fileParser.generateGroupedNodeStructure()

        result shouldBe expectedOutput
    }

    @Suppress("LongMethod")
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
            "checkout__payment__credit_card__cvv" to "CVV",
        )
        val fileParser = fileParser(fileContent = mapContent)

        val expectedOutput = mapOf(
            "Activation" to nodeGroup(
                "ForgottenPassword" to nodeGroup(
                    "Birthdate" to nodeGroup(
                        "cancelButton" to nodeItem(
                            "activation__forgotten_password__birthdate__cancel_button",
                            "Cancel",
                        ),
                    ),
                    "emailInput" to nodeItem(
                        "activation__forgotten_password__email_input",
                        "Enter your email",
                    ),
                ),
            ),
            "Home" to nodeGroup(
                "welcomeMessage" to nodeItem(
                    "home__welcome_message",
                    "Welcome to our application!",
                ),
            ),
            "Profile" to nodeGroup(
                "Settings" to nodeGroup(
                    "Privacy" to nodeGroup(
                        "title" to nodeItem(
                            "profile__settings__privacy__title",
                            "Privacy Settings",
                        ),
                        "description" to nodeItem(
                            "profile__settings__privacy__description",
                            "Manage your privacy settings here.",
                        ),
                    ),
                ),
            ),
            "Checkout" to nodeGroup(
                "Payment" to nodeGroup(
                    "CreditCard" to nodeGroup(
                        "numberInput" to nodeItem(
                            "checkout__payment__credit_card__number_input",
                            "Credit Card Number",
                        ),
                        "expiryDate" to nodeItem(
                            "checkout__payment__credit_card__expiry_date",
                            "Expiry Date",
                        ),
                        "cvv" to nodeItem("checkout__payment__credit_card__cvv", "CVV"),
                    ),
                ),
            ),
        )

        val result = fileParser.generateGroupedNodeStructure()

        result shouldBe expectedOutput
    }

    @Test
    fun `generateNestedMapStructure with repetitive key elements creates valid nested map`() {
        val mapContent: Map<String, String> = mapOf(
            "profile__settings__privacy__privacy_policy" to "Privacy Policy",
            "profile__settings__privacy__privacy_policy__details" to "Detailed description",
        )
        val fileParser = fileParser(fileContent = mapContent)

        val expectedOutput = mapOf(
            "Profile" to nodeGroup(
                "Settings" to nodeGroup(
                    "Privacy" to nodeGroup(
                        "privacyPolicy" to nodeItem(
                            "profile__settings__privacy__privacy_policy",
                            "Privacy Policy",
                        ),
                        "PrivacyPolicy" to nodeGroup(
                            "details" to nodeItem(
                                "profile__settings__privacy__privacy_policy__details",
                                "Detailed description",
                            ),
                        ),
                    ),
                ),
            ),
        )

        val result = fileParser.generateGroupedNodeStructure()

        result shouldBe expectedOutput
    }

    @Test
    fun `generateNestedMapStructure with deeply nested structures creates expected map`() {
        val mapContent: Map<String, String> = mapOf(
            "system__config__database__settings__max_connections" to "100",
            "system__config__database__settings__timeout" to "30",
        )
        val fileParser = fileParser(fileContent = mapContent)

        val expectedOutput = mapOf(
            "System" to nodeGroup(
                "Config" to nodeGroup(
                    "Database" to nodeGroup(
                        "Settings" to nodeGroup(
                            "maxConnections" to nodeItem(
                                "system__config__database__settings__max_connections",
                                "100",
                            ),
                            "timeout" to nodeItem(
                                "system__config__database__settings__timeout",
                                "30",
                            ),
                        ),
                    ),
                ),
            ),
        )

        val result = fileParser.generateGroupedNodeStructure()

        result shouldBe expectedOutput
    }

    @Test
    fun `generateNestedMapStructure with non-standard characters in keys handles correctly`() {
        val mapContent: Map<String, String> = mapOf(
            "user__name__first name" to "John",
            "user__name__last-name" to "Doe",
        )
        val fileParser = fileParser(fileContent = mapContent)

        val expectedOutput = mapOf(
            "User" to nodeGroup(
                "Name" to nodeGroup(
                    "first name" to nodeItem("user__name__first name", "John"),
                    "last-name" to nodeItem("user__name__last-name", "Doe"),
                ),
            ),
        )

        val result = fileParser.generateGroupedNodeStructure()

        result shouldBe expectedOutput
    }

    private fun fileParser(
        fileContent: Map<String, String> = mockk(),
        minorDelimiter: String = "_",
        majorDelimiter: String = "__",
    ) = FileParser(
        fileContent = fileContent,
        minorDelimiter = minorDelimiter,
        majorDelimiter = majorDelimiter,
    )

    private fun nodeGroup(vararg children: Pair<String, Node>): Node.Group =
        Node.Group(children.toMap())

    private fun nodeItem(originalKey: String, value: String): Node.Item =
        Node.Item(
            key = originalKey,
            translation = Translation.Text(value),
        )
}
