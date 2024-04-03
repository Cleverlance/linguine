package com.qinshift.fileReader

import io.mockk.every
import io.mockk.mockk
import org.gradle.internal.impldep.junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.Test

class FileReaderTest {

		@Test
		fun `read should return a correct map when given a JSON file pat`() {
			val fileContentReader = mockk<FileContentReader>()

			val filePath = "path/to/test.json"
			val fileContent = """
            {
                "test__file__input_value": "Input Value",
                "another__file__description_value": "Description"
            }
        """.trimIndent()

			every { fileContentReader.readText(filePath) } returns fileContent

			val fileReader = FileReader(fileContentReader)
			val result = fileReader.read(filePath, FileType.JSON)
			val expectedResult = mapOf(
				"test__file__input_value" to "Input Value",
				"another__file__description_value" to "Description"
			)

			assertEquals(expectedResult, result)
		}
}