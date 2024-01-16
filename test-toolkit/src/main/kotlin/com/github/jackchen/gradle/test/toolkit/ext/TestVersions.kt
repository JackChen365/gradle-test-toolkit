package com.github.jackchen.gradle.test.toolkit.ext

/**
 * The class that store the data from [TestVersion]
 * You can find it in [com.github.jackchen.gradle.test.toolkit.testdsl.TestProjectRunner]
 */
class TestVersions(
    val supportedAndroidVersion: String = SUPPORTED_ANDROID_VERSION,
    val supportedGradleVersion: String = SUPPORTED_GRADLE_VERSION,
    val supportedKotlinPluginVersion: String = SUPPORTED_KOTLIN_PLUGIN_VERSION
) {
    companion object {
        const val SUPPORTED_ANDROID_VERSION = "8.2.0"
        const val SUPPORTED_GRADLE_VERSION = "8.5"
        const val SUPPORTED_KOTLIN_PLUGIN_VERSION = "1.9.21"
    }
}
