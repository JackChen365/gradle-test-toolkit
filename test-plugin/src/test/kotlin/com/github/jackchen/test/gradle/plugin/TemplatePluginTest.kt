package com.github.jackchen.test.gradle.plugin

import com.github.jackchen.gradle.test.toolkit.GradlePluginTest
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TemplatePluginTest : GradlePluginTest() {

    private fun testProjectSetup(closure: TemplatePluginTest.() -> Unit) {
        kotlinAndroidTemplate {
            template {
                plugins {
                    id("com.test.plugin").version(testPluginVersion())
                }
                dependencies {
                    implementation("androidx.core:core-ktx:1.7.0")
                    implementation("androidx.appcompat:appcompat:1.4.1")
                }
            }
        }
        apply(closure)
    }

    @Test
    fun buildTest() {
        testProjectSetup {
            build(":app:processDebugResources") {
                Assertions.assertEquals(TaskOutcome.SUCCESS, task(":app:processDebugResources")?.outcome)
            }
            build("compileDebugJavaWithJavac") {
                Assertions.assertEquals(TaskOutcome.SUCCESS, task(":app:compileDebugJavaWithJavac")?.outcome)
            }
        }
    }
}
