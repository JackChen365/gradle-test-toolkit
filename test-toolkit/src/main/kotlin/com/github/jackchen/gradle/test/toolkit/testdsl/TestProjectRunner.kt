package com.github.jackchen.gradle.test.toolkit.testdsl

import com.github.jackchen.gradle.test.toolkit.ext.TestVersions
import com.github.jackchen.gradle.test.toolkit.runner.GradleRunnerProvider
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File

/**
 * * The test project runner. This is a simple wrapper class for [GradleRunner].
 * It supports executing the Gradle task for us in test methods
 * there are two methods for us:
 * [build]
 * [buildAndFail]
 *
 * @param testVersions The test versions. Including android version, kotlin version and gradle version.
 * @param gradleRunnerProvider The gradle runner provider. We will use this provider to helps us create the [GradleRunner]
 * @param testTempDir The project dir.
 */
open class TestProjectRunner(
    val testVersions: TestVersions,
    gradleRunnerProvider: GradleRunnerProvider,
    testTempDir: File
) {
    private val gradleRunner: GradleRunner
    val projectDir: File

    init {
        // Prepare the test repo
        testTempDir.deleteRecursively()
        projectDir = testTempDir
        if (!projectDir.exists()) {
            projectDir.mkdirs()
        }

        // Prepare the Gradle runner.
        gradleRunner = gradleRunnerProvider.getGradleRunner(projectDir, testVersions.supportedGradleVersion)
    }

    fun build(
        buildArguments: Array<out String>,
        assertions: BuildResult.() -> Unit = {}
    ) {
        val buildArgumentList = mutableListOf<String>()
        buildArgumentList.add("clean")
        buildArgumentList.add("--stacktrace")
        buildArgumentList.addAll(buildArguments)
        gradleRunner
            .withArguments(buildArgumentList)
            .build()
            .run { assertions() }
    }

    fun buildAndFail(
        buildArguments: Array<out String>,
        assertions: BuildResult.() -> Unit = {}
    ) {
        val buildArgumentList = mutableListOf<String>()
        buildArgumentList.add("clean")
        buildArgumentList.add("--stacktrace")
        buildArgumentList.addAll(buildArguments)
        gradleRunner
            .withArguments(buildArgumentList)
            .buildAndFail()
            .run { assertions() }
    }
}
