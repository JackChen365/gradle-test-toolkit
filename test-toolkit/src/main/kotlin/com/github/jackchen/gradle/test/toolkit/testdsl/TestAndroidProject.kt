package com.github.jackchen.gradle.test.toolkit.testdsl

import com.github.jackchen.gradle.test.toolkit.sdklocator.AndroidSDKLocator

/**
 * The Android test project. This class helps you create the test project and generate the local.properties
 */
open class TestAndroidProject(runner: TestProjectRunner) : TestGradleProject(runner) {
    fun getAndroidVersion() = runner.testVersions.supportedAndroidVersion

    override fun project(closure: TestProject.() -> Unit) {
        val testProject = TestProject(runner)
        testProject.module("app") {
            dir("src/main") {
                dir("java")
                dir("kotlin")
            }
        }
        generateLocalProperties(testProject)
        generateSettings(testProject)
    }

    fun generateLocalProperties(testProject: TestProject) {
        val androidSdkDir = AndroidSDKLocator.guessSdkDir()
        if (null == androidSdkDir) {
            throw RuntimeException("Can not find the android.sdk. Please add sdk.dir in local.properties or add ANDROID_HOME in system env.")
        } else {
            testProject.file("local.properties") {
                "sdk.dir=$androidSdkDir"
            }
        }
    }
}
