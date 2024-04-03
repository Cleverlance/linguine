package com.qinshift

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.test.Test

class FileWriterTest {
	@Test
	fun `writeToFile creates directories and writes content correctly`(@TempDir tempDir: Path) {
		val fileWriter = FileWriter()
		val testContent = StringBuilder("This is a test.")
		val nestedFilePath = tempDir.resolve("nested/dir/testOutput.txt").toFile()

		nestedFilePath.parentFile.exists() shouldBe false

		fileWriter.writeToFile(nestedFilePath.absolutePath, testContent)

		nestedFilePath.parentFile.exists() shouldBe true

		val writtenContent = nestedFilePath.readText()

		testContent.toString() shouldBe writtenContent
	}
}