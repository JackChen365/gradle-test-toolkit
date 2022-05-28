package com.github.jackchen.gradle.test.toolkit.runner

import org.gradle.testkit.runner.GradleRunner
import java.io.File

/**
 * The default gradle runner provider.
 * It helps you configure the project and gradle version.
 * We use the gradle runner in [com.github.jackchen.gradle.test.toolkit.GradlePluginTest.build] and
 * [com.github.jackchen.gradle.test.toolkit.GradlePluginTest.buildAndFail]. So you can test it easily.
 *
 * If you want to have your own [GradleRunnerProvider], take a look at this annotation [TestGradleRunner]
 * and you can use it like this:
 * <p>
 *     @TestGradleRunner(DefaultGradleRunnerProvider::class.java)
 *     internal class TestClass : GradlePluginTest() {
 *          ...
 *     }
 * </p>
 */
class DefaultGradleRunnerProvider : GradleRunnerProvider {
    companion object {
        private val sharedTestKitDir = File(".").resolve(".gradle").absoluteFile.also {
            if (!it.exists()) it.mkdir()
        }
    }

    override fun getGradleRunner(projectDir: File, gradleVersion: String): GradleRunner {
        return GradleRunner.create().withProjectDir(projectDir).withGradleVersion(gradleVersion)
            .withTestKitDir(sharedTestKitDir).withDebug(true).forwardOutput()
    }
}