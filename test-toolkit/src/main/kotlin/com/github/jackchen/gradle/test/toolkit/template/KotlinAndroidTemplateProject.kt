package com.github.jackchen.gradle.test.toolkit.template

import com.github.jackchen.gradle.test.toolkit.testdsl.TestProjectRunner

/**
 * Generate the Android test project using Kotlin script.
 */
class KotlinAndroidTemplateProject(runner: TestProjectRunner) : TestAndroidTemplateProject(runner) {

    override fun project(closure: TestProject.() -> Unit) {
        val testProject = TestProject(runner)
        closure(testProject)
        generateLocalProperties(testProject)
    }

    fun androidProject(closure: AndroidTemplateCompositionDSL.() -> Unit) {
        val templateComposition = object : AndroidTemplateCompositionDSL(runner) {
            //Only process the template
            override fun project(closure: TestProject.() -> Unit) = Unit
        }
        closure(templateComposition)

        project {
            module("app") {
                file("build.gradle.kts") {
                    val jvmVersion = templateComposition.gradleTemplate.buildInfo.jvmVersion
                    """
                    |plugins {
                    |${templateComposition.gradleTemplate.pluginSet.joinToString("\n") { "\tid(\"${it.id}\")" }}
                    |${templateComposition.gradleTemplate.pluginsBlock}
                    |}
                    |
                    |android {
                    |    namespace = "${templateComposition.gradleTemplate.buildInfo.namespace}"
                    |    compileSdk = ${templateComposition.gradleTemplate.buildInfo.compileSdk}
                    |
                    |    defaultConfig {
                    |        applicationId = "${templateComposition.gradleTemplate.packageInfo.packageName}"
                    |        minSdk = ${templateComposition.gradleTemplate.buildInfo.minSdk}
                    |        targetSdk = ${templateComposition.gradleTemplate.buildInfo.targetSdk}
                    |        versionCode = ${templateComposition.gradleTemplate.buildInfo.versionCode}
                    |        versionName = "${templateComposition.gradleTemplate.buildInfo.versionName}"
                    |
                    |        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    |    }
                    |    compileOptions {
                    |        sourceCompatibility = JavaVersion.VERSION_${jvmVersion.replace(".", "_")}
                    |        targetCompatibility = JavaVersion.VERSION_${jvmVersion.replace(".", "_")}
                    |    }
                    |    kotlinOptions {
                    |        jvmTarget = "$jvmVersion"
                    |    }
                    |${templateComposition.gradleTemplate.androidBlock}
                    |}
                    |
                    |dependencies {
                    |${templateComposition.gradleTemplate.dependencyList.joinToString("\n") { "\t${it.configurationName}(${it.dependencyNotation})" }}
                    |${templateComposition.gradleTemplate.dependencyBlock}
                    |}
                    """.trimMargin()
                }
                kotlinSourceDir(templateComposition.gradleTemplate.packageInfo.packageName) {
                    file("MainActivity.kt") {
                        """
                        |package ${templateComposition.gradleTemplate.packageInfo.packageName}
                        |import androidx.appcompat.app.AppCompatActivity
                        |class MainActivity : AppCompatActivity()
                        """.trimMargin()
                    }
                }
                dir("src/main") {
                    file("AndroidManifest.xml") {
                        """
                        |<?xml version="1.0" encoding="utf-8"?>
                        |<manifest xmlns:android="http://schemas.android.com/apk/res/android">
                        |    <application
                        |        android:allowBackup="true"
                        |        android:label="${templateComposition.gradleTemplate.packageInfo.name}"
                        |        android:theme="@style/Theme.AppCompat">
                        |        <activity
                        |            android:name=".MainActivity"
                        |            android:exported="true">
                        |            <intent-filter>
                        |                <action android:name="android.intent.action.MAIN" />
                        |                <category android:name="android.intent.category.LAUNCHER" />
                        |            </intent-filter>
                        |        </activity>
                        |    </application>
                        |</manifest>
                        """.trimMargin()
                    }
                }
            }
            settingGradleKts {
                """
                |pluginManagement {
                |    repositories {
                |        gradlePluginPortal()
                | ${templateComposition.gradleTemplate.repositorySet.joinToString("\n") { "\t\t$it" }}
                |    }
                |}
                |dependencyResolutionManagement {
                |    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
                |    repositories {
                |${templateComposition.gradleTemplate.repositorySet.joinToString("\n") { "\t\t$it" }}
                |    }
                |}
                |rootProject.name = "${templateComposition.gradleTemplate.packageInfo.name}"
                |include(":app")
                """.trimMargin()
            }
            buildGradleKts {
                """
                |// Top-level build file where you can add configuration options common to all sub-projects/modules.
                |plugins {
                |${templateComposition.gradleTemplate.pluginSet.joinToString("\n") { "\tid(\"${it.id}\").version(\"${it.version}\") apply false" }}
                |}
                |
                |tasks.register("clean", Delete::class) {
                |    delete(rootProject.buildDir)
                |}
                """.trimMargin()
            }
            if (templateComposition.gradleTemplate.propertySet.isNotEmpty()) {
                gradleProperties {
                    """
                    |${templateComposition.gradleTemplate.propertySet.joinToString("\n")}
                    """.trimMargin()
                }
            }
        }
        //Process the custom project file.
        closure(AndroidTemplateCompositionDSL(runner))
    }
}