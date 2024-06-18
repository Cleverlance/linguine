package io.github.cleverlance.linguine.linguinegenerator

class FileParser(
    private val fileContent: Map<String, String>,
    private val minorDelimiter: String,
    private val majorDelimiter: String,
) {
    fun generateGroupedMapStructure(): Map<String, Map<String, Any>> {
        val groupedMap = mutableMapOf<String, MutableMap<String, Pair<String, String>>>()
        fileContent.forEach { (key, value) ->
            val firstSegment =
                key.substringBefore(majorDelimiter).replaceFirstChar { it.uppercaseChar() }
            val restOfKey = key.substringAfter(majorDelimiter)
            groupedMap.computeIfAbsent(firstSegment) { mutableMapOf() }[restOfKey] = key to value
        }
        return groupedMap.mapValues { (_, map) -> generateNestedMapStructure(map) }
    }

    private fun generateNestedMapStructure(map: Map<String, Pair<String, String>>): Map<String, Any> {
        val root = mutableMapOf<String, Any>()
        map.forEach { (key, value) ->
            val parts = transformKeyToCamelCaseSegments(key)
            updateNestedMapStructure(root, parts, value)
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
        value: Pair<String, String>,
    ) {
        var current = root
        @Suppress("UNCHECKED_CAST")
        parts.forEachIndexed { index, part ->
            val formattedPart = formatPart(part, index < parts.lastIndex)
            if (index == parts.lastIndex) {
                current[formattedPart] = value
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
