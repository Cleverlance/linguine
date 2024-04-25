package io.github.cleverlance.linguine.linguinegenerator

class FileParser(
    private val fileContent: Map<String, String>,
    private val minorDelimiter: String,
    private val majorDelimiter: String,
) {
    fun generateNestedMapStructure(): Map<String, Any> {
        val root = mutableMapOf<String, Any>()
        fileContent.keys.forEach { key ->
            val parts = transformKeyToCamelCaseSegments(key)
            updateNestedMapStructure(root, parts, key)
        }
        return root
    }

    private fun transformKeyToCamelCaseSegments(key: String): List<String> {
        return key.split(majorDelimiter).map { hierarchicalSegment ->
            hierarchicalSegment.split(minorDelimiter).joinToString("") { word ->
                word.replaceFirstChar { it.uppercaseChar() }
            }
        }
    }

    private fun updateNestedMapStructure(
        root: MutableMap<String, Any>,
        parts: List<String>,
        fullKey: String,
    ) {
        var current = root
        @Suppress("UNCHECKED_CAST")
        parts.forEachIndexed { index, part ->
            val formattedPart = formatPart(part, index < parts.lastIndex)
            if (index == parts.lastIndex) {
                current[formattedPart] = fullKey
            } else {
                current =
                    current.computeIfAbsent(formattedPart) { mutableMapOf<String, Any>() } as MutableMap<String, Any>
            }
        }
    }

    private fun formatPart(part: String, isLast: Boolean): String {
        return if (isLast) {
            part.replaceFirstChar { it.uppercaseChar() }
        } else {
            part.replaceFirstChar { it.lowercase() }
        }
    }
}
