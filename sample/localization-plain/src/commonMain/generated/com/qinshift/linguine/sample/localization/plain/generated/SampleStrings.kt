package com.qinshift.linguine.sample.localization.plain.generated

import com.qinshift.linguine.linguineruntime.presentation.Localiser.localise
import kotlin.String

public object SampleStrings {
    public object Plain {
        public val sentence: String
            get() = localise("sample_all_forms__plain__sentence")

        public val fallbackSentence: String
            get() = localise("sample_all_forms__plain__fallback_sentence")

        public fun itemSentence(param1: String): String =
            localise("sample_all_forms__plain__item_sentence", param1)

        public fun orderedSentence(
            param1: String,
            param2: String,
            param3: String,
        ): String = localise("sample_all_forms__plain__ordered_sentence", param1, param2, param3)

        public fun reversedSentence(
            param1: String,
            param2: String,
            param3: String,
        ): String = localise("sample_all_forms__plain__reversed_sentence", param1, param2, param3)

        public fun fallbackPlaceholders(
            param1: String,
            param2: String,
            param3: String,
        ): String = localise("sample_all_forms__plain__fallback_placeholders", param1, param2, param3)
    }
}
