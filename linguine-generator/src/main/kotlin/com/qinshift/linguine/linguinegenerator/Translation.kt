package com.qinshift.linguine.linguinegenerator

public sealed interface Translation {
    public data class Text(val value: String) : Translation
    public data class Plural(val forms: Map<String, String>) : Translation
}
