package com.github.jackchen.gradle.test.toolkit.template

import com.github.jackchen.gradle.test.toolkit.GradlePluginTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File

internal class KotlinAndroidTemplateProjectTest : GradlePluginTest() {
    @Test
    fun testGradleTemplateDSL() {
        kotlinAndroidTemplate {
            template {
                `package` {
                    name = "test-app"
                    packageName = "com.android.test"
                }
                build {
                    targetSdk = 31
                    targetSdk = 31
                    minSdk = 21
                }
                properties {
                    property("android.useAndroidX=true")
                }
                repositories {
                    repo("google()")
                    repo("mavenCentral()")
                }
                plugins {
                    id("com.android.application").version("4.2.0")
                    id("org.jetbrains.kotlin.android").version("1.6.21")
                }
                dependencies {
                    implementation("androidx.core:core-ktx:1.7.0")
                    implementation("androidx.appcompat:appcompat:1.4.1")
                }
            }
        }
        Assertions.assertTrue(File(testProjectRunner.projectDir, "build.gradle.kts").exists())
        Assertions.assertTrue(File(testProjectRunner.projectDir, "settings.gradle.kts").exists())
        val gradlePropertyFile = File(testProjectRunner.projectDir, "gradle.properties")
        Assertions.assertTrue(gradlePropertyFile.exists())
        Assertions.assertEquals("android.useAndroidX=true", gradlePropertyFile.readText().trim())
    }
}