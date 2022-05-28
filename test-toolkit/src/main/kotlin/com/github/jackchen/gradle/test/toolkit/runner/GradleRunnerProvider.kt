package com.github.jackchen.gradle.test.toolkit.runner

import com.github.jackchen.gradle.test.toolkit.testdsl.TestProjectRunner
import org.gradle.testkit.runner.GradleRunner
import java.io.File

/**
 * An [GradleRunnerProvider] is responsible for [GradleRunner] providing
 *
 * <p>An [GradleRunnerProvider] can be registered via the
 * [TestGradleRunner] annotation either in the test class or in [TestProjectRunner]
 *
 * <p>Implementations must provide a no-args constructor.
 */
interface GradleRunnerProvider {
    fun getGradleRunner(projectDir: File, gradleVersion: String): GradleRunner
}