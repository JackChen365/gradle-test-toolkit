package com.github.jackchen.gradle.test.toolkit.runner

import kotlin.reflect.KClass

/**
 * [TestGradleRunner] can be used in non-private field [com.github.jackchen.gradle.test.toolkit.testdsl.TestProjectRunner]
 * and test class.
 * We use it to configure the gradle runner.
 * For example:
 * <p>
 *     @TestGradleRunner(DefaultGradleRunnerProvider::class.java)
 *     internal class TestClass : GradlePluginTest() {
 *          ...
 *     }
 * </p>
 *
 * The default gradle runner provider only simply functions.
 *
 * <p>
 *     class DefaultGradleRunnerProvider : GradleRunnerProvider {
 *          override fun getGradleRunner(projectDir: File, gradleVersion: String): GradleRunner {
 *              return GradleRunner.create().withProjectDir(projectDir).withGradleVersion(gradleVersion)
 *                  .withTestKitDir(sharedTestKitDir).withDebug(true).forwardOutput()
 *          }
 *     }
 * </p>
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class TestGradleRunner(
    /**
     * The type of [GradleRunnerProvider] to be used.
     */
    val value: KClass<out GradleRunnerProvider>
)