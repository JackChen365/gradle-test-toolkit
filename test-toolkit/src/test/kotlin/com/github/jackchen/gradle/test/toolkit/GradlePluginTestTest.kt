package com.github.jackchen.gradle.test.toolkit

import com.github.jackchen.gradle.test.toolkit.ext.TestVersion
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File

internal class GradlePluginTestTest : GradlePluginTest() {

    @Test
    @TestVersion(androidVersion = "8.2.0", gradleVersion = "8.5", kotlinVersion = "1.9.21")
    fun androidProjectTest() {
        androidProject {
            module("app") {
                sourceDir("com.test") {
                    file("Main.kt") {
                        """
                        fun main(){
                            println("Hello world")
                        }
                        """.trimIndent()
                    }
                }
            }
            gradleProperties { "android.useAndroidX=true" }
            settingGradle {
                """
                pluginManagement {
                    repositories {
                        gradlePluginPortal()
                        google()
                        mavenCentral()
                    }
                }
                dependencyResolutionManagement {
                    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
                    repositories {
                        google()
                        mavenCentral()
                    }
                }
                rootProject.name = "test-app"
                include ':app'
                """.trimIndent()
            }
            buildGradle {
                """
                plugins {
                    id 'com.android.application' version '${androidVersion()}' apply false
                    id 'org.jetbrains.kotlin.jvm' version '${kotlinVersion()}' apply false
                }
                """.trimIndent()
            }
        }
        val projectDir = testProjectRunner.projectDir
        Assertions.assertTrue(File(projectDir, "app/src/main/kotlin/com/test/Main.kt").exists())
        Assertions.assertTrue(File(projectDir, "build.gradle").exists())
        Assertions.assertTrue(File(projectDir, "gradle.properties").exists())
        Assertions.assertTrue(File(projectDir, "settings.gradle").exists())
    }

    @Test
    fun gradleProjectTest() {
        gradleProject {
            sourceDir("com.test") {
                dir("dir/dir2") {
                    file("gradle.properties") {
                        """kotlin.code.style=official"""
                    }
                }
                file("Main.kt") {
                    """
                    fun main(){
                        println("Hello world")
                    }
                    """.trimIndent()
                }
            }
        }
        gradleProject {
            sourceDir("com.test") {
                file("Main.kt") {
                    """
                    fun main(){
                        println("Hello world")
                    }
                    """.trimIndent()
                }
            }
            settingGradleKts {
                """
                import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
                plugins {
                    kotlin("jvm") version "1.9.21"
                }
                group = "org.example"
                version = "1.0-SNAPSHOT"
                repositories {
                    mavenCentral()
                }
                dependencies {
                    testImplementation(kotlin("test"))
                }
                tasks.test {
                    useJUnitPlatform()
                }
                tasks.withType<KotlinCompile> {
                    kotlinOptions.jvmTarget = "17"
                }
                """.trimIndent()
            }
            gradleProperties {
                """
                kotlin.code.style=official
                """.trimIndent()
            }
            buildGradleKts {
                """
                plugins {
                    id 'com.android.application' version '${androidVersion()}' apply false
                    id 'org.jetbrains.kotlin.jvm' version '${kotlinVersion()}' apply false
                }
                """.trimIndent()
            }
        }
        val projectDir = testProjectRunner.projectDir
        Assertions.assertTrue(File(projectDir, "src/main/kotlin/com/test/Main.kt").exists())
        Assertions.assertTrue(File(projectDir, "build.gradle.kts").exists())
        Assertions.assertTrue(File(projectDir, "gradle.properties").exists())
        Assertions.assertTrue(File(projectDir, "settings.gradle.kts").exists())
    }

    private fun testProjectSetup() {
        kotlinAndroidTemplate {
            template {
                `package` {
                    name = "test-app"
                    packageName = "com.android.test"
                }
                build {
                    targetSdk = 34
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
                    id("com.android.application").version(androidVersion())
                    id("org.jetbrains.kotlin.android").version(kotlinVersion())
                }
                dependencies {
                    implementation("androidx.core:core-ktx:1.7.0")
                    implementation("androidx.appcompat:appcompat:1.4.1")
                }
            }
        }
    }

    @Test
    fun testGradleTemplateDSL() {
        testProjectSetup()
        build(":app:processDebugResources") {
            Assertions.assertEquals(TaskOutcome.SUCCESS, task(":app:processDebugResources")?.outcome)
        }
        build(":app:compileDebugKotlin") {
            Assertions.assertEquals(TaskOutcome.SUCCESS, task(":app:compileDebugKotlin")?.outcome)
        }
    }
}