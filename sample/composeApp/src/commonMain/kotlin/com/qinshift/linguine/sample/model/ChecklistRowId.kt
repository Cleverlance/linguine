package com.qinshift.linguine.sample.model

internal sealed interface ChecklistRowId {
    val key: String
    val expectedKey: String
    val usesEnglishFallback: Boolean

    enum class Plain(
        override val key: String,
        override val expectedKey: String,
        override val usesEnglishFallback: Boolean = false,
    ) : ChecklistRowId {
        Sentence(
            key = "sentence",
            expectedKey = "sentence",
        ),
        FallbackSentence(
            key = "fallback",
            expectedKey = "fallbackSentence",
            usesEnglishFallback = true,
        ),
        SinglePlaceholder(
            key = "single-placeholder",
            expectedKey = "itemSentence",
        ),
        OrderedPlaceholders(
            key = "ordered-placeholders",
            expectedKey = "orderedSentence",
        ),
        ReversedPlaceholders(
            key = "reversed-placeholders",
            expectedKey = "reversedSentence",
        ),
        FallbackPlaceholders(
            key = "fallback-placeholders",
            expectedKey = "fallbackPlaceholders",
            usesEnglishFallback = true,
        ),
    }

    enum class Plural(
        override val key: String,
        override val expectedKey: String,
        override val usesEnglishFallback: Boolean = false,
    ) : ChecklistRowId {
        AllFormsItemCount(
            key = "plural-items",
            expectedKey = "itemCount",
        ),
        AllFormsSparsePlaceholders(
            key = "plural-sparse-placeholders",
            expectedKey = "sparsePlaceholders",
        ),
        AllFormsCategoryName(
            key = "plural-category",
            expectedKey = "categoryName",
        ),
        AllFormsFallbackCount(
            key = "plural-fallback",
            expectedKey = "fallbackCount",
            usesEnglishFallback = true,
        ),
        OtherOnlyItemCount(
            key = "plural-items",
            expectedKey = "itemCount",
        ),
        OtherOnlyReversedPlaceholders(
            key = "plural-reversed-placeholders",
            expectedKey = "reversedPlaceholders",
        ),
        OtherOnlyCategoryName(
            key = "plural-category",
            expectedKey = "categoryName",
        ),
        OtherOnlyFallbackCount(
            key = "plural-language-fallback",
            expectedKey = "fallbackCount",
            usesEnglishFallback = true,
        ),
    }
}
