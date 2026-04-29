package com.qinshift.linguine.linguinegenerator

public class FileParser(
    fileContent: Map<String, *>,
    private val minorDelimiter: String,
    private val majorDelimiter: String,
) {
    private val fileContent: Map<String, Translation> = fileContent.mapValues { (key, value) ->
        when (value) {
            is Translation -> value
            is String -> Translation.Text(value)
            else -> error("Unsupported translation value for key '$key': $value")
        }
    }

    public fun generateGroupedNodeStructure(): Map<String, Node.Group> {
        val groupedMap = mutableMapOf<String, MutableMap<String, Node.Item>>()
        fileContent.forEach { (key, value) ->
            val groupName: String
            val nestedKey: String

            if (key.contains(majorDelimiter)) {
                groupName = key.substringBefore(majorDelimiter).toPascalCase(minorDelimiter)
                nestedKey = key.substringAfter(majorDelimiter)
            } else {
                groupName = key.toPascalCase(minorDelimiter)
                nestedKey = key
            }

            groupedMap.computeIfAbsent(groupName) { mutableMapOf() }[nestedKey] = Node.Item(
                key = key,
                translation = value,
            )
        }

        return groupedMap.mapValues { (_, map) -> generateNestedNodeStructure(map) }
    }

    private fun generateNestedNodeStructure(map: Map<String, Node.Item>): Node.Group {
        return map.entries.fold(Node.Group(emptyMap())) { root, (key, value) ->
            val parts = transformKeyToCamelCaseSegments(key)
            insertLeaf(root, parts, value)
        }
    }

    private fun transformKeyToCamelCaseSegments(key: String): List<String> {
        return key.split(majorDelimiter).map { hierarchicalSegment ->
            hierarchicalSegment.split(minorDelimiter).joinToString("") { word ->
                word.replaceFirstChar { it.uppercaseChar() }
            }
        }
    }

    private fun insertLeaf(
        root: Node.Group,
        parts: List<String>,
        value: Node.Item,
        index: Int = 0,
    ): Node.Group {
        val formattedPart = formatPart(parts[index], index < parts.lastIndex)

        if (index == parts.lastIndex) {
            return root.copy(
                children = root.children + (formattedPart to value),
            )
        }

        val nestedGroup = when (val existingNode = root.children[formattedPart]) {
            null -> Node.Group(emptyMap())
            is Node.Group -> existingNode
            is Node.Item -> error("Expected group node but found leaf ${existingNode.key}.")
        }

        val entry = formattedPart to insertLeaf(
            root = nestedGroup,
            parts = parts,
            value = value,
            index = index + 1,
        )
        return root.copy(
            children = root.children + entry,
        )
    }

    private fun formatPart(part: String, isIntermediate: Boolean): String {
        return if (isIntermediate) {
            part.replaceFirstChar { it.uppercaseChar() }
        } else {
            part.replaceFirstChar { it.lowercase() }
        }
    }

    private fun String.toPascalCase(delimiter: String): String {
        return split(delimiter).joinToString("") { word -> word.replaceFirstChar(Char::uppercaseChar) }
    }
}
