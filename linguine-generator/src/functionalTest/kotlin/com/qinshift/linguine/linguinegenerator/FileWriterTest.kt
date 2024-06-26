package com.qinshift.linguine.linguinegenerator

import io.kotest.matchers.shouldBe
import java.io.File
import java.nio.file.Path
import kotlin.test.BeforeTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class FileWriterTest {

    private lateinit var fileWriter: FileWriter

    @TempDir
    lateinit var tempDir: Path

    @BeforeTest
    fun setUp() {
        fileWriter = FileWriter()
    }

    private fun assertFileContentMatches(file: File, expectedContent: String) {
        file.exists() shouldBe true
        val readContent = file.readText()
        readContent shouldBe expectedContent
    }

    @Test
    fun `writeToFile writes correct text to the file`() {
        val testFile = File(tempDir.toFile(), "testFileWriter.kt")
        val testContent = "This is a test string to be written\n."

        fileWriter.writeToFile(testFile, testContent)

        assertFileContentMatches(testFile, testContent)
    }

    @Test
    fun `writeToFile should create file if it does not exist`() {
        val nonExistentFile = File(tempDir.toFile(), "nonexistent/subdirectory/testFile.txt")
        val contentToWrite = "Test content"

        nonExistentFile.exists() shouldBe false

        fileWriter.writeToFile(nonExistentFile, contentToWrite)

        assertFileContentMatches(nonExistentFile, contentToWrite)
    }
}
