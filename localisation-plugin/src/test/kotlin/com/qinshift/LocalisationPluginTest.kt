package com.qinshift

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class LocalisationPluginTest {

    @Test
    fun pluginRegistersATask() {
        // Create a test project and apply the plugin
        val project: Project = ProjectBuilder.builder().build()
        project.plugins.apply("com.qinshift.linguine")

        // Verify the result
        assertNotNull(project.tasks.findByName("loc"))
    }

    @Test
    fun pluginShouldBeConfigured() {
        val project: Project = ProjectBuilder.builder().build()

        project.pluginManager.apply("com.qinshift.linguine")
        val extension = project.extensions.getByType(LocalisationExtension::class.java)
        extension.jsonFilePath = "src/commonMain/resources/string.json"
        extension.outputDirPath = "presentation"
        extension.stringsFileName = "Strings.kt"

        assertEquals("src/commonMain/resources/string.json", extension.jsonFilePath)
        assertEquals("presentation", extension.outputDirPath)
        assertEquals("Strings.kt", extension.stringsFileName)
    }
}
