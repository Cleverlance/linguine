package com.qinshift.project

import com.qinshift.linguine.linguineruntime.presentation.Localiser
import presentation.Demo

class Greeting {
    private val platform = getPlatform()

    fun greet(greetingNumber: Int): String {
        return Demo.hello("${greetingNumber}", platform.name)
    }

    fun alternativeGreet(greetingNumber: Int): String {
        return Localiser.localise("demo__hello", "${greetingNumber}", platform.name)
    }
}