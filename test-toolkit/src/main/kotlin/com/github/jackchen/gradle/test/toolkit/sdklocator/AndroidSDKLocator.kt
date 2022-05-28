package com.github.jackchen.gradle.test.toolkit.sdklocator

import java.io.File
import java.util.*

/**
 * AndroidSDKLocator contains the logic to select the android SDK based on the variables set by the user.
 *
 * <p>Use [AndroidSDKLocator.guessSdkDir] to fetch the android sdk path,
 * we first guess the Android sdk from the local.properties from current path to the root path.
 * If we didn't find the android SDK path. We will try to find it in system variables.
 *
 * Is there any other good idea to get the AndroidSDK path? Please let me know.
 */
object AndroidSDKLocator {
    private const val SDK_DIR = "sdk.dir"

    private fun readLocalPropertiesValue(): String? {
        var localPropertyFile = File("local.properties")
        while (!localPropertyFile.exists()) {
            localPropertyFile = File(localPropertyFile.absoluteFile.parentFile.parentFile, "local.properties")
        }
        val properties = Properties()
        properties.load(localPropertyFile.inputStream())
        return properties.getProperty(SDK_DIR)
    }

    private fun locateAndroidSdkFromSystem(): String? {
        arrayOf(
            System.getenv("ANDROID_SDK"),
            System.getenv("ANDROID_HOME")
        ).forEach { dir ->
            if (dir != null && File(dir).exists()) {
                return dir
            }
        }
        return null
    }

    fun guessSdkDir(): String? {
        return readLocalPropertiesValue() ?: locateAndroidSdkFromSystem()
    }
}