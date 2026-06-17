package presentation

import com.qinshift.linguine.linguineruntime.presentation.Localiser.localise
import kotlin.String

public object Demo {
    public val buttonLabel: String = localise("demo__button_label")

    public fun hello(param1: String, param2: String): String = localise("demo__hello", param1,
            param2)
}
