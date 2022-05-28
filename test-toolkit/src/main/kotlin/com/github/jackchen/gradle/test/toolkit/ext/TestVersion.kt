package com.github.jackchen.gradle.test.toolkit.ext

/**
 * [TestVersion] can be used in non-private field [com.github.jackchen.gradle.test.toolkit.testdsl.TestProjectRunner]
 * and test method.
 *
 * The priority to apply this annotaiton:
 * 1. From the test method
 * 2. From the test field, which is [com.github.jackchen.gradle.test.toolkit.testdsl.TestProjectRunner]
 *
 * All those three versions are used inside the generated test project by [com.github.jackchen.gradle.test.toolkit.GradlePluginTestTest]
 * You can use those methods in your custom test project as well
 * 1. [com.github.jackchen.gradle.test.toolkit.GradlePluginTest.androidVersion]
 * 2. [com.github.jackchen.gradle.test.toolkit.GradlePluginTest.kotlinVersion]
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class TestVersion(
    /**
     * The support android build tool version. The default version is "4.2.1"
     */
    val androidVersion: String = TestVersions.SUPPORTED_ANDROID_VERSION,
    /**
     * The support gradle version. The default version is "6.9.1"
     */
    val gradleVersion: String = TestVersions.SUPPORTED_GRADLE_VERSION,
    /**
     * The support kotlin version. The default version is "1.6.10"
     */
    val kotlinVersion: String = TestVersions.SUPPORTED_KOTLIN_PLUGIN_VERSION
)