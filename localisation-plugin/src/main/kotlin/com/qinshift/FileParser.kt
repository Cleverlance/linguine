package com.qinshift

class FileParser(
    private val fileContent: Map<String, String>,
    private val minorDelimiter: String,
    private val majorDelimiter: String
) {
    fun generateNestedMapStructureFromJSON(): MutableMap<String, Any> {
        val root = mutableMapOf<String, Any>()
        fileContent.keys.forEach { key ->
            val parts = transformKeyToCamelCaseSegments(key, minorDelimiter, majorDelimiter)
            updateNestedMapStructure(root, parts, key)
        }
        return root
    }

    private fun transformKeyToCamelCaseSegments(
        key: String,
        minorDelimiter: String,
        majorDelimiter: String
    ): List<String> {
        return key.split(majorDelimiter).map { hierarchicalSegment ->
            hierarchicalSegment.split(minorDelimiter).joinToString("") { word ->
                word.replaceFirstChar { it.uppercaseChar() }
            }
        }
    }

    private fun updateNestedMapStructure(
        root: MutableMap<String, Any>,
        parts: List<String>,
        fullKey: String
    ) {
        var current = root
        @Suppress("UNCHECKED_CAST")
        parts.forEachIndexed { index, part ->
            val formattedPart = formatPart(part, index, parts)
            if (index == parts.lastIndex) {
                current[formattedPart] = fullKey
            } else {
                current =
                    current.computeIfAbsent(formattedPart) { mutableMapOf<String, Any>() } as MutableMap<String, Any>
            }
        }
    }

    private fun formatPart(part: String, index: Int, parts: List<String>): String {
        return if (index < parts.lastIndex) {
            part.replaceFirstChar { it.uppercaseChar() }
        } else {
            part.replaceFirstChar { it.lowercase() }
        }
    }
}