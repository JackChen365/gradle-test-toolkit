package com.github.jackchen.gradle.test.toolkit.template

import com.github.jackchen.gradle.test.toolkit.testdsl.TestProjectRunner

/**
 * Generate the Android test project using Groovy script.
 *
 */
class GroovyAndroidTemplateProject(runner: TestProjectRunner) : TestAndroidTemplateProject(runner) {

    override fun project(closure: TestProject.() -> Unit) {
        val testProject = TestProject(runner)
        closure(testProject)
        generateLocalProperties(testProject)
    }

    fun androidProject(closure: AndroidTemplateCompositionDSL.() -> Unit) {
        val templateComposition = AndroidTemplateCompositionDSL(runner)
        closure(templateComposition)
        project {
            module("app") {
                file("build.gradle") {
                    """
                    |plugins {
                    |${templateComposition.gradleTemplate.pluginSet.joinToString("\n") { "\tid: '${it.id}' version '${it.version}'" }}
                    |}
                    |android {
                    |    compileSdk ${templateComposition.gradleTemplate.buildInfo.compileSdk}
                    |
                    |    defaultConfig {
                    |        applicationId ${templateComposition.gradleTemplate.packageInfo.packageName}
                    |        minSdk ${templateComposition.gradleTemplate.buildInfo.minSdk}
                    |        targetSdk ${templateComposition.gradleTemplate.buildInfo.targetSdk}
                    |        versionCode 1
                    |        versionName "1.0"
                    |
                    |        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
                    |    }
                    |    compileOptions {
                    |        sourceCompatibility JavaVersion.VERSION_1_8
                    |        targetCompatibility JavaVersion.VERSION_1_8
                    |    }
                    |    kotlinOptions {
                    |        jvmTarget = '1.8'
                    |    }
                    |}
                    |dependencies {
                    |${templateComposition.gradleTemplate.dependencyList.joinToString("\n") { "\t${it.configurationName} '${it.dependencyNotation}'" }}
                    |}
                    """.trimMargin()
                }
                sourceDir(templateComposition.gradleTemplate.packageInfo.packageName) {
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
                        |<manifest xmlns:android="http://schemas.android.com/apk/res/android"
                        |    package="${templateComposition.gradleTemplate.packageInfo.packageName}">
                        |
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
            settingGradle {
                """
                |pluginManagement {
                |    repositories {
                |        gradlePluginPortal()
                |${templateComposition.gradleTemplate.repositorySet.joinToString("\n") { "\t\t$it" }}
                |    }
                |}
                |dependencyResolutionManagement {
                |    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
                |    repositories {
                |${templateComposition.gradleTemplate.repositorySet.joinToString("\n") { "\t\t$it" }}
                |    }
                |}
                |rootProject.name = "${templateComposition.gradleTemplate.packageInfo.name}"
                |include ':app'
                """.trimMargin()
            }
            buildGradle {
                """
                |plugins {
                |${templateComposition.gradleTemplate.pluginSet.joinToString("\n") { "\tid: '${it.id}' version '${it.version}' apply false" }}
                |}
                """.trimMargin()
            }
            gradleProperties {
                """
                |${templateComposition.gradleTemplate.propertySet.joinToString("\n")}
                """.trimMargin()
            }
        }
    }
}