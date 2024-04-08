package com.qinshift.fileReader

import java.io.File
import org.gradle.internal.impldep.junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class FileReaderTest {

    @TempDir
    lateinit var testProjectDir: File

    @Test
    fun `read should return a correct map when given a JSON file pat`() {
        val file = File(testProjectDir, "test.json").apply {
            writeText(
                """
            {
                "test__file__input_value": "Input Value",
                "another__file__description_value": "Description"
            }
        """.trimIndent()
            )
        }

        val fileReader = FileReader()
        val result = fileReader.read(file, FileType.JSON)
        val expectedResult = mapOf(
            "test__file__input_value" to "Input Value",
            "another__file__description_value" to "Description"
        )

        assertEquals(expectedResult, result)
    }
}