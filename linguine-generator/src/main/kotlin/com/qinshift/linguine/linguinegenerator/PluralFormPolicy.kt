package com.qinshift.linguine.linguinegenerator

public enum class PluralFormPolicy(
    internal val requiredForms: List<String>,
) {
    REQUIRE_ALL_FORMS(listOf("zero", "one", "two", "few", "many", "other")),
    REQUIRE_OTHER_ONLY(listOf("other"))
}
