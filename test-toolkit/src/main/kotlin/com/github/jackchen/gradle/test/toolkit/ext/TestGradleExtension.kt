package com.github.jackchen.gradle.test.toolkit.ext

import com.github.jackchen.gradle.test.toolkit.runner.DefaultGradleRunnerProvider
import com.github.jackchen.gradle.test.toolkit.runner.TestGradleRunner
import com.github.jackchen.gradle.test.toolkit.testdsl.TestProjectRunner
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionConfigurationException
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.platform.commons.util.ExceptionUtils
import org.junit.platform.commons.util.Preconditions
import org.junit.platform.commons.util.ReflectionUtils
import java.io.File
import java.lang.reflect.Field
import java.util.function.Predicate

/**
 * `TestGradleExtension` is a JUnit Jupiter extension that help us configure the test project and temp folder
 *
 * @see TestVersion The test version including the Kotlin version, Gradle version and Android-build tool-version
 * @see TestGradleRunner The test gradle runner
 * @see TestTempDir The test temp dir
 */
class TestGradleExtension : BeforeEachCallback, AfterEachCallback {
    /**
     * Perform field injection for non-private, `static` fields (i.e.,
     * class fields) of type [TestProjectRunner] that are annotated with
     * [TestVersion] and [TestTempDir]
     */
    @Throws(Exception::class)
    override fun beforeEach(context: ExtensionContext) {
        injectFields(context, context.requiredTestInstance) { member: Field? -> ReflectionUtils.isNotStatic(member) }
    }

    private fun injectFields(context: ExtensionContext, testInstance: Any, predicate: Predicate<Field>) {
        val fieldList = ReflectionUtils.findFields(
            context.requiredTestClass,
            { field: Field -> field.type == TestProjectRunner::class.java },
            ReflectionUtils.HierarchyTraversalMode.TOP_DOWN
        )
        val testWithCacheAnnotation = context.testMethod.get().getAnnotation(TestWithCache::class.java)
            ?: context.testInstance.javaClass.getAnnotation(TestWithCache::class.java)
        val withCache = null != testWithCacheAnnotation && testWithCacheAnnotation.value
        Preconditions.condition(fieldList.size == 1, "We can only use one test project for the test class.")
        fieldList.forEach { field ->
            predicate.test(field)
            assertValidFieldCandidate(field) // The annotation priority is:
            // 1. From the test class
            // 2. From the field (Here is mean our testProject)
            val testGradleRunnerAnnotation = testInstance.javaClass.getAnnotation(TestGradleRunner::class.java)
                ?: context.testMethod.get().annotations.firstOrNull { it is TestGradleRunner } as? TestGradleRunner
            val gradleRunnerProvider = if (null != testGradleRunnerAnnotation) {
                testGradleRunnerAnnotation.value.java.getConstructor().newInstance()
            } else DefaultGradleRunnerProvider() // The annotation priority is: // 1. From the test method
            // 2. From the field (Here is mean our testProject)
            val testVersionAnnotation =
                context.testMethod.get().annotations.firstOrNull { it is TestVersion } as? TestVersion
                    ?: field.getAnnotation(TestVersion::class.java)
            val testVersions = if (null != testVersionAnnotation) TestVersions(
                supportedAndroidVersion = testVersionAnnotation.androidVersion,
                supportedGradleVersion = testVersionAnnotation.gradleVersion,
                supportedKotlinPluginVersion = testVersionAnnotation.kotlinVersion
            ) else TestVersions()

            val testTempDirAnnotation = field.getAnnotation(TestTempDir::class.java)
            val testTempDir =
                if (null != testTempDirAnnotation) File(testTempDirAnnotation.value) else File("build/tmp").apply { if (!exists()) mkdirs() }
            if (!withCache) {
                testTempDir.deleteRecursively()
            }
            try {
                ReflectionUtils.makeAccessible(field)[testInstance] =
                    TestProjectRunner(testVersions, gradleRunnerProvider, testTempDir)
            } catch (t: Throwable) {
                ExceptionUtils.throwAsUncheckedException(t)
            }
        }
        val tempFileFieldList = ReflectionUtils.findFields(
            context.requiredTestClass,
            { field: Field -> field.type == File::class.java },
            ReflectionUtils.HierarchyTraversalMode.TOP_DOWN
        )
        tempFileFieldList.forEach { field ->
            predicate.test(field)
            val testTempDirAnnotation = field.getAnnotation(TestTempDir::class.java)
            val testTempDir =
                if (null != testTempDirAnnotation) File(testTempDirAnnotation.value) else File("build/tmp").apply { if (!exists()) mkdirs() }
            if (!withCache) {
                testTempDir.deleteRecursively()
            }
            try {
                ReflectionUtils.makeAccessible(field)[testInstance] = testTempDir
            } catch (t: Throwable) {
                ExceptionUtils.throwAsUncheckedException(t)
            }
        }
    }

    private fun assertValidFieldCandidate(field: Field) {
        assertSupportedType("field", field.type)
        if (ReflectionUtils.isPrivate(field)) {
            throw ExtensionConfigurationException("Field [$field] must not be private.")
        }
    }

    private fun assertSupportedType(target: String, type: Class<*>) {
        if (type != TestProjectRunner::class.java) {
            throw ExtensionConfigurationException(
                "Can only resolve [@[KtLintTest, @TestVersion and @TestTempDir] $target of type " + TestProjectRunner::class.java.name + " but was: " + type.name
            )
        }
    }

    override fun afterEach(context: ExtensionContext) {
        val testWithCacheAnnotation = context.testMethod.get().getAnnotation(TestWithCache::class.java)
            ?: context.testInstance.javaClass.getAnnotation(TestWithCache::class.java)
        if (null == testWithCacheAnnotation || !testWithCacheAnnotation.value) {
            cleanup(context)
        }
    }

    private fun cleanup(context: ExtensionContext) {
        val fieldList = ReflectionUtils.findFields(
            context.requiredTestClass,
            { field: Field -> field.type == TestProjectRunner::class.java },
            ReflectionUtils.HierarchyTraversalMode.TOP_DOWN
        )
        fieldList.forEach { field ->
            val testTempDirAnnotation = field.getAnnotation(TestTempDir::class.java)
            val testTempDir =
                if (null != testTempDirAnnotation) File(testTempDirAnnotation.value) else File("build/tmp").apply { if (!exists()) mkdirs() }
            testTempDir.deleteRecursively()
        }
        val tempFileFieldList = ReflectionUtils.findFields(
            context.requiredTestClass,
            { field: Field -> field.type == File::class.java },
            ReflectionUtils.HierarchyTraversalMode.TOP_DOWN
        )
        tempFileFieldList.forEach { field ->
            val testTempDirAnnotation = field.getAnnotation(TestTempDir::class.java)
            val testTempDir =
                if (null != testTempDirAnnotation) File(testTempDirAnnotation.value) else File("build/tmp").apply { if (!exists()) mkdirs() }
            testTempDir.deleteRecursively()
        }
    }
}
