package com.github.jackchen.gradle.test.toolkit.internal

import org.gradle.api.Project
import java.io.File
import java.util.*

/**
 * Build variable will helps us manage all the global Gradle properties.
 * For example:
 * CI when we build package from Github action. We got a variable: CI and it always true.
 *
 * Each variable could configure by four ways
 * 1. Gradle properties. gradle.properties. or ./gradlew -Pproperty_name=property_value
 * 2. local.properties
 * 3. [System.getenv] from shell system variable.
 *
 * We use get variable from this order.
 */
fun Project.findBuildProperty(name: String): String? {
    return findBuildPropertyFromLocalProperty(name) ?: findProperty(name) as String? ?: System.getenv(name)
}

private fun Project.findBuildPropertyFromLocalProperty(name: String): String? {
    val localProperties = File(rootProject.projectDir, "local.properties")
    if (localProperties.exists()) {
        return localProperties.inputStream().use {
            val properties = Properties()
            properties.load(it)
            properties.getProperty(name)
        }
    }
    return null
}

val Project.versionName: Boolean
    get() = findBuildProperty("VERSION_NAME").toBoolean()