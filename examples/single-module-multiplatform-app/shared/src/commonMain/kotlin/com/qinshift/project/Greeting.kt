package com.qinshift.project

import com.qinshift.linguine.linguineruntime.presentation.Localiser
import presentation.Demo

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return Demo.hello(platform.name)
    }


//    Using "Localizer.localize" directly is possible as well
    fun greet2(): String {
        return Localiser.localise("demo__hello", platform.name)
    }
}