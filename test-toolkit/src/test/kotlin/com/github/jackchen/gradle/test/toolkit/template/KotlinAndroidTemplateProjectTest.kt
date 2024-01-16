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
                    compileSdk = 34
                    targetSdk = 34
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
                    id("com.android.application").version("8.2.0")
                    id("org.jetbrains.kotlin.android").version("1.9.21")
                }
                dependencies {
                    implementation("androidx.core:core-ktx:1.7.0")
                    implementation("androidx.appcompat:appcompat:1.4.1")
                }
            }
            project {
                module("app") {
                    file("build.gradle.kts") {
                        """
                            plugins {
                            	id("com.android.application")
                            	id("org.jetbrains.kotlin.android")
                            	id("build.config.hook")
                            }
                            android {
                                namespace = "com.android.test"
                                compileSdk = 34
                                defaultConfig {
                                    applicationId = "com.android.test"
                                    minSdk = 21
                                    targetSdk = 34
                                    versionCode = 1
                                    versionName = "1.0"

                                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                                    buildConfigField "int", "VERSION_CODE", "1.0.1"
                                    buildConfigField "String", "VERSION_NAME", "101"
                                }
                                compileOptions {
                                    sourceCompatibility = JavaVersion.VERSION_1_8
                                    targetCompatibility = JavaVersion.VERSION_1_8
                                }
                                kotlinOptions {
                                    jvmTarget = "1.8"
                                }
                            }
                            dependencies {
                            	implementation("androidx.core:core-ktx:1.7.0")
                            	implementation("androidx.appcompat:appcompat:1.4.1")
                            	implementation("io.github.jackchen365:gradle-test-toolkit:1.0.2")
                            }
                        """.trimIndent()
                    }
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