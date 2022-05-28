package com.github.jackchen.gradle.test.toolkit.template

import com.github.jackchen.gradle.test.toolkit.testdsl.TestAndroidProject
import com.github.jackchen.gradle.test.toolkit.testdsl.TestProjectIntegration.TestProject
import com.github.jackchen.gradle.test.toolkit.testdsl.TestProjectRunner

/**
 * The basic Android Template project
 * We combine the [AndroidTemplateDSL] and [TestProject] to support both AndroidTemplate and TestProject functions.
 * This means you can use the AndroidTemplate and the TestProject to generate your custom test file.
 *
 */
open class TestAndroidTemplateProject(runner: TestProjectRunner) : TestAndroidProject(runner) {

    class AndroidTemplateCompositionDSL(private val runner: TestProjectRunner) {
        internal val gradleTemplate = AndroidTemplateDSL(runner)

        fun project(closure: TestProject.() -> Unit) {
            val testProject = TestProject(runner)
            closure(testProject)
        }

        fun template(closure: AndroidTemplateDSL.() -> Unit) = closure(gradleTemplate)
    }
}