package com.github.jackchen.gradle.test.toolkit

import com.github.jackchen.gradle.test.toolkit.ext.TestGradleExtension
import com.github.jackchen.gradle.test.toolkit.ext.TestTempDir
import com.github.jackchen.gradle.test.toolkit.ext.TestVersion
import com.github.jackchen.gradle.test.toolkit.runner.DefaultGradleRunnerProvider
import com.github.jackchen.gradle.test.toolkit.runner.TestGradleRunner
import com.github.jackchen.gradle.test.toolkit.template.AndroidTemplateDSL
import com.github.jackchen.gradle.test.toolkit.template.GroovyAndroidTemplateProject
import com.github.jackchen.gradle.test.toolkit.template.KotlinAndroidTemplateProject
import com.github.jackchen.gradle.test.toolkit.template.TestAndroidTemplateProject
import com.github.jackchen.gradle.test.toolkit.testdsl.TestGradleProject
import com.github.jackchen.gradle.test.toolkit.testdsl.TestProjectIntegration
import com.github.jackchen.gradle.test.toolkit.testdsl.TestProjectRunner
import io.github.jackchen___.test_toolkit.BuildConfig
import org.gradle.testkit.runner.BuildResult
import org.junit.jupiter.api.extension.ExtendWith
import java.io.File
import java.io.FileNotFoundException

/**
 * The Gradle Plugin test class.
 * This class is responsible for providing the test function for testing the Gradle plugin and generating the test project.
 *
 * We use [TestGradleExtension] to inject the [TestProjectRunner]. So you can use it in your test method.
 *
 * @see [testPluginVersion] The current version for this project. Make sure it is snapshot version if you want to test it.
 * @see [kotlinVersion] The kotlin version that from [TestVersion]
 * @see [androidVersion] The android build tool version that from [TestVersion]
 */
@ExtendWith(TestGradleExtension::class)
abstract class GradlePluginTest {
    @TestTempDir
    @TestVersion
    @TestGradleRunner(DefaultGradleRunnerProvider::class)
    lateinit var testProjectRunner: TestProjectRunner
    lateinit var projectDir: File

    fun testPluginVersion(): String {
        //Mast be snapshot, otherwise the code change won't work because we use a local maven.
        return "${BuildConfig.APP_VERSION}-SNAPSHOT"
    }

    fun kotlinVersion(): String = testProjectRunner.testVersions.supportedKotlinPluginVersion

    fun androidVersion(): String = testProjectRunner.testVersions.supportedAndroidVersion

    /**
     * The basic integration function to generate the Gradle test project.
     * It will help you create a "settings.gradle" by default.
     */
    fun gradleProject(closure: TestProjectIntegration.TestProject.() -> Unit) {
        val testGradleProject = TestGradleProject(testProjectRunner)
        testGradleProject.project(closure)
    }

    /**
     * The basic integration function to generate the Android test project.
     * It will help you create a "settings.gradle" and local.properties by default.
     */
    fun androidProject(closure: TestProjectIntegration.TestProject.() -> Unit) {
        val testGradleProject = TestGradleProject(testProjectRunner)
        testGradleProject.project(closure)
    }

    /**
     * The integration function that with a template path.
     * We will help you copy the template to the test dir and you can use the DSL class: [TestProjectIntegration] to generate your custom test files.
     * Besides, It will help you create a "settings.gradle" and local.properties by default.
     */
    fun androidProject(templatePath: String, closure: TestProjectIntegration.TestProject.() -> Unit) =
        androidProject(File(templatePath), closure)

    fun androidProject(templateDir: File, closure: TestProjectIntegration.TestProject.() -> Unit) {
        if (!templateDir.exists()) {
            throw FileNotFoundException("The template dir does not exist.")
        }
        val projectDir = testProjectRunner.projectDir
        projectDir.deleteRecursively()
        projectDir.mkdirs()
        templateDir.copyRecursively(projectDir)
        val testGradleProject = TestGradleProject(testProjectRunner)
        testGradleProject.project(closure)
    }

    /**
     * The android template that using groovy script style.
     * This is different from the [TestProjectIntegration]. We have simplified the DSL only for the Android test project.
     * Besides, The [TestAndroidTemplateProject.AndroidTemplateCompositionDSL] has combined the [TestProjectIntegration] and the [AndroidTemplateDSL]
     * Therefore, you can use template and custom file together.
     */
    fun androidTemplate(closure: TestAndroidTemplateProject.AndroidTemplateCompositionDSL.() -> Unit) {
        val androidTemplateProject = GroovyAndroidTemplateProject(testProjectRunner)
        androidTemplateProject.androidProject(closure)
    }

    /**
     * The android template that using kotlin script style.
     * This is different from the [TestProjectIntegration]. We have simplified the DSL only for the Android test project.
     * Besides, The [TestAndroidTemplateProject.AndroidTemplateCompositionDSL] has combined the [TestProjectIntegration] and the [AndroidTemplateDSL]
     * Therefore, you can use template and custom file together.
     */
    fun kotlinAndroidTemplate(closure: TestAndroidTemplateProject.AndroidTemplateCompositionDSL.() -> Unit) {
        val androidTemplateProject = KotlinAndroidTemplateProject(testProjectRunner)
        androidTemplateProject.androidProject(closure)
    }

    /**
     * Execute a gradle task and expect it success.
     */
    fun build(
        vararg buildArguments: String,
        assertions: BuildResult.() -> Unit = {}
    ) {
        testProjectRunner.build(buildArguments, assertions)
    }

    /**
     * Execute a gradle task and expect it failed.
     */
    fun buildAndFail(
        vararg buildArguments: String,
        assertions: BuildResult.() -> Unit = {}
    ) {
        testProjectRunner.buildAndFail(buildArguments, assertions)
    }
}
