package com.github.jackchen.gradle.test.toolkit.testdsl

import java.io.File

/**
 * The test Gradle project. This integration class will help you generate the `settings.gradle` file.
 */
open class TestGradleProject(val runner: TestProjectRunner) : TestProjectIntegration() {
    val projectDir: File = runner.projectDir

    fun getKotlinVersion() = runner.testVersions.supportedKotlinPluginVersion

    override fun project(closure: TestProject.() -> Unit) {
        val testProject = TestProject(runner)
        closure(testProject)
        generateSettings(testProject)
    }

    /**
     * Generate the settings.gradle file based on the modules in TestProject
     */
    internal fun generateSettings(testProject: TestProject) {
        val projectName = testProject.projectDir.name
        val settings = File(testProject.projectDir, "settings.gradle")
        val includeList = testProject.moduleList.joinToString("\n") { "include ':${it.name}'" }
        settings.writeText(
            """
            |dependencyResolutionManagement {
            |    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
            |    repositories {
            |        google()
            |        mavenCentral()
            |    }
            |}
            |rootProject.name = "$projectName"
            |$includeList
            """.trimMargin()
        )
    }
}
