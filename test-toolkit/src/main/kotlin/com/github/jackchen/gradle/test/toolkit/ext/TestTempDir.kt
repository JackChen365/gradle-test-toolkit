package com.github.jackchen.gradle.test.toolkit.ext

/**
 * [TestTempDir] can be used to annotate a non-private field in a test class.
 * Now we use it in [java.io.File] and [com.github.jackchen.gradle.test.toolkit.testdsl.TestProjectRunner]
 *
 * <p>Please note that [TestTempDir] is not supported on constructor
 * parameters. Please use field injection instead, by annotating a non-private
 * instance field with [TestTempDir].
 *
 * Its value is related to this project.
 * For example: the default value: "build/tmp" will be path_to_your_repo/build/tmp
 *
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class TestTempDir(val value: String = "build/tmp")
