package io.github.cleverlance.linguine.linguinegenerator

import io.github.cleverlance.linguine.linguine_generator.BuildConfig
import io.github.cleverlance.linguine.linguinegenerator.filereader.FileReader
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.work.Incremental
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import io.github.cleverlance.linguine.linguinegenerator.filereader.FileType as LinguineFileType
import org.gradle.api.provider.Property as GradleProperty

@Suppress("unused")
class LinguinePlugin : Plugin<Project> {

    private companion object {
        const val GENERATE_STRINGS_TASK_NAME = "generateStrings"
        const val RUNTIME_DEPENDENCY = "${BuildConfig.GROUP}:linguine-runtime:${BuildConfig.VERSION}"
    }

    override fun apply(project: Project) {
        val extension = project.extensions.create("linguineConfig", LinguineConfig::class.java)

        val isKmp = project.hasAnyPlugin("org.jetbrains.kotlin.multiplatform")
        val isAndroid = project.hasAnyPlugin("com.android.application", "com.android.library")
        val isJava = project.hasAnyPlugin("org.jetbrains.kotlin.jvm", "java")

        when {
            isKmp -> configureForKmp(project, extension)
            isAndroid -> configureForAndroid(project, extension)
            isJava -> configureForJvm(project, extension)
        }


        project.subprojects.forEach { subproject ->
            subproject.afterEvaluate {
                if (extension.moduleNameMapping.containsKey(subproject.name)) {
                    val keyPrefix = extension.moduleNameMapping[subproject.name]
                    subproject.tasks.register("generateModuleStrings", GenerateStringsTask::class.java) {
                        val moduleSpecificInputFile = "${extension.inputFilePath}/${keyPrefix}_strings.json"
                        inputFile.set(subproject.layout.projectDirectory.file(moduleSpecificInputFile))
                        outputFile.set(subproject.layout.buildDirectory.file("$keyPrefix.kt"))
                    }
                }
            }
        }

        project.tasks.register(GENERATE_STRINGS_TASK_NAME, GenerateStringsTask::class.java) {
            inputFile.set(project.layout.projectDirectory.file(extension.inputFilePath))
            fileType.set(extension.inputFileType)
            majorDelimiter.set(extension.majorDelimiter)
            minorDelimiter.set(extension.minorDelimiter)
        }
    }

    private fun Project.hasAnyPlugin(vararg plugins: String): Boolean =
        plugins.any(this.plugins::hasPlugin)

    private fun configureForKmp(project: Project, extension: LinguineConfig) {
        with(project.extensions.getByType<KotlinMultiplatformExtension>()) {
            sourceSets.commonMain.dependencies {
                implementation(RUNTIME_DEPENDENCY)
            }
        }
        configureGenerateStringsTask(project, extension)
    }

    private fun configureForAndroid(project: Project, extension: LinguineConfig) {
        project.dependencies {
            add("implementation", RUNTIME_DEPENDENCY)
        }
        configureGenerateStringsTask(project, extension)
    }


    private fun configureForJvm(project: Project, extension: LinguineConfig) {
        project.dependencies {
            add("implementation", RUNTIME_DEPENDENCY)
        }
        configureGenerateStringsTask(project, extension)
    }

    private fun configureGenerateStringsTask(project: Project, extension: LinguineConfig) {
        project.afterEvaluate {
            val buildTasks = extension.buildTaskName?.let { name -> listOf(task(name)) }
                ?: tasks.filter { task -> task.name.startsWith("compile") }

            buildTasks.forEach { task -> task.dependsOn(GENERATE_STRINGS_TASK_NAME) }
        }
    }
}

@CacheableTask
abstract class GenerateStringsTask : DefaultTask() {
    @get:Incremental
    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputFile: RegularFileProperty

    @get:Input
    abstract val fileType: GradleProperty<LinguineFileType>

    @get:Input
    abstract val minorDelimiter: GradleProperty<String>

    @get:Input
    abstract val majorDelimiter: GradleProperty<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun generate() {
        // Read Input File
        val fileContent = FileReader().read(
            file = inputFile.asFile.get(),
            fileType = fileType.get(),
        )

        // Parse File into internal nested object structure
        val root = FileParser(
            fileContent = fileContent,
            minorDelimiter = minorDelimiter.get(),
            majorDelimiter = majorDelimiter.get(),
        ).generateNestedMapStructure()

        val categorizedContent = root.keys.groupBy { it.split("__").first() }
            .mapValues { (_, keys) -> keys.associateWith { root[it] } }

        categorizedContent.forEach { (moduleName, content) ->
            val outputPath = project.layout.buildDirectory.file(
                "${project.extensions.getByType<LinguineConfig>().outputFilePath}/$moduleName.kt"
            ).get().asFile
            val contentAsString: Map<String, String> = content.filterValues { it != null }.mapValues { it.value.toString() }
            val contentAsAny: Map<String, Any> = content.filterValues { it != null }.mapValues { it.value!! }
            val outputFileContent =
                FileContentGenerator(outputPath.toPath(), contentAsString).generateFileContent(contentAsAny)
            FileWriter().writeToFile(outputPath, outputFileContent)
        }

        logger.lifecycle("Linguine: Files have been successfully created in the directory ${project.layout.buildDirectory}")
    }
}