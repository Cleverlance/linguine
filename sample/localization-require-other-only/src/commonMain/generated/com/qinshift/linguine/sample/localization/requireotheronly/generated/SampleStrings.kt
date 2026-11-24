package com.qinshift.linguine.sample.localization.requireotheronly.generated

import com.qinshift.linguine.linguineruntime.presentation.Localiser.localisePlural
import kotlin.Number
import kotlin.String

public object SampleStrings {
    public object Plural {
        public fun itemCount(
            count: Number,
            param1: String,
        ): String = localisePlural("sample_other_only__plural__item_count", count, param1)

        public fun reversedPlaceholders(
            count: Number,
            param1: String,
            param2: String,
            param3: String,
        ): String = localisePlural("sample_other_only__plural__reversed_placeholders", count, param1,
                param2, param3)

        public fun categoryName(
            count: Number,
            param1: String,
            param2: String,
            param3: String,
            param4: String,
        ): String = localisePlural("sample_other_only__plural__category_name", count, param1, param2,
                param3, param4)

        public fun fallbackCount(
            count: Number,
            param1: String,
            param2: String,
            param3: String,
            param4: String,
        ): String = localisePlural("sample_other_only__plural__fallback_count", count, param1, param2,
                param3, param4)
    }
}
