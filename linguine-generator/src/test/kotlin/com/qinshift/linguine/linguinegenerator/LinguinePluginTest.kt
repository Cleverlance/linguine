package com.qinshift.linguine.linguinegenerator

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class LinguinePluginTest {

    @Test
    fun `plugin registers a task`() {
        // Create a test project and apply the plugin
        val project: Project = ProjectBuilder.builder().build()
        project.plugins.apply("com.qinshift.linguine")
        val extension = project.extensions.getByType(LinguineConfig::class.java)
        extension.inputFilePath = "src/commonMain/resources/string.json"
        extension.outputFilePath = "presentation"
        extension.outputFileName = "Strings.kt"

        // Verify the result
        assertNotNull(project.tasks.findByName("generateStringsObject"))
    }

    @Test
    fun `given configuration plugin should be configured`() {
        val project: Project = ProjectBuilder.builder().build()

        project.pluginManager.apply("com.qinshift.linguine")
        val extension = project.extensions.getByType(LinguineConfig::class.java)
        extension.inputFilePath = "src/commonMain/resources/string.json"
        extension.outputFilePath = "presentation"
        extension.outputFileName = "Strings.kt"

        assertEquals("src/commonMain/resources/string.json", extension.inputFilePath)
        assertEquals("presentation", extension.outputFilePath)
        assertEquals("Strings.kt", extension.outputFileName)
    }
}
