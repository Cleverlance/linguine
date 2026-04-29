package com.qinshift.linguine.linguinegenerator

public sealed interface Node {
    public data class Group(val children: Map<String, Node>) : Node
    public data class Item(val key: String, val translation: Translation) : Node
}
