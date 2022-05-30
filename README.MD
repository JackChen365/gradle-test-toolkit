# Ktlint Gradle

**Provides the function to generate a Gradle project for us to test your Gradle plugin**

Latest plugin version: [1.0.1]

## Table of content

- [How to use](#how-to-use)
    - [Compile](#Compile)
    - [Using GradlePluginTest to use DSL to generate your test project](#using-gradleplugintest-to-use-dsl-to-generate-your-test-project)
    - [Case1 using default DSL to generate your test project](#case1-using-default-dsl-to-generate-your-test-project)
    - [Case2 Mix the DSL with an existed project](#case2-mix-the-dsl-with-an-existed-project)
    - [Case3 Use the default template DSL to generate an android project](#case3-use-the-default-template-dsl-to-generate-an-android-project)
- [Samples](#samples)
- [Code style](#Code-style)
- [FAQ](#faq)
- [Links](#links)

## How to use

#### Simple setup

#### Compile

<details>
<summary>Kotlin</summary>

```kotlin
repositories {
    mavenCentral()
}
dependencies {
    testImplementation("io.github.jackchen365:gradle-test-toolkit:1.0.1")
}
```

</details>

#### Using GradlePluginTest to use DSL to generate your test project

<details>
<summary>Kotlin</summary>

```kotlin
class TemplatePluginTest : GradlePluginTest() {
    private fun testProjectSetup(closure: TemplatePluginTest.() -> Unit) {
        kotlinAndroidTemplate {
            template {
                plugins {
                    id("com.test.plugin").version(File("../VERSION_CURRENT.txt").readText().trim())
                }
                dependencies {
                    implementation("androidx.core:core-ktx:1.7.0")
                    implementation("androidx.appcompat:appcompat:1.4.1")
                }
            }
        }
        TemplatePluginTest().apply(closure)
    }

    @Test
    fun buildTest() {
        testProjectSetup {
            build(":app:prebuild") {
                Assertions.assertEquals(TaskOutcome.SUCCESS, task(":app:prebuild")?.outcome)
            }
            build(":app:lint") {
                Assertions.assertEquals(TaskOutcome.FAILED, task(":app:lint")?.outcome)
            }
        }
    }
}

```

</details>

#### Case1 using default DSL to generate your test project

<details>
<summary>Kotlin</summary>

```kotlin
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
    settingGradle {
        """
        pluginManagement {
            repositories {
                gradlePluginPortal()
                google()
                mavenCentral()
            }
        }
        """.trimIndent()
    }
    gradleProperties { "android.useAndroidX=true" }
}
```

</details>

#### Case2 Mix the DSL with an existed project

<details>
<summary>Kotlin</summary>

```kotlin

androidProject("src/test/inputs/test-app") {
    module("app") {
        kotlinSourceDir("test") {
            file("Main.kt") {
                """
                fun main(){
                    println("Hello world")
                }
                """.trimIndent()
            }
        }
    }
}
Assertions.assertTrue(File(projectDir, "app/src/main/kotlin/test/Main.kt").exists())
Assertions.assertTrue(File(projectDir, "app/build.gradle").exists())

```

</details>

#### Case3 Use the default template DSL to generate an android project

<details>
<summary>Kotlin</summary>

```kotlin

androidTemplateKts {
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
            id("com.android.application").version(androidVersion())
            id("org.jetbrains.kotlin.android").version(kotlinVersion())
        }
        dependencies {
            implementation("androidx.core:core-ktx:1.7.0")
            implementation("androidx.appcompat:appcompat:1.4.1")
        }
    }
}

```

</details>

#### Samples

Optionally apply plugin to all project modules:
<details>
<summary>Kotlin</summary>

```kotlin
class TemplatePluginTest : GradlePluginTest() {

    private fun testProjectSetup(closure: TemplatePluginTest.() -> Unit) {
        kotlinAndroidTemplate {
            template {
                plugins {
                    id("com.test.plugin").version(File("../VERSION_CURRENT.txt").readText().trim())
                }
                dependencies {
                    implementation("androidx.core:core-ktx:1.7.0")
                    implementation("androidx.appcompat:appcompat:1.4.1")
                }
            }
        }
        TemplatePluginTest().apply(closure)
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
```

</details>

- [test-plugin](/test-plugin) - A Gradle plugin uses the `test-toolkit` to do the integration testing.

#### Code style

We use `ktlint` to help us format the code. You can use the tasks below to check your code before commit

`addKtlintCheckGitPreCommitHook` - adds Git pre-commit hook, that runs ktlint check over staged files.
`addKtlintFormatGitPreCommitHook` - adds Git pre-commit hook, that runs ktlint format over staged files and adds fixed
files back to commit.

#### FAQ

* Why we need it?

  It's tough to test the Gradle plugin and especially with Gradle 7.0. When we use `gradleTestKit()` we are facing
  several problem.
    * Can not debug the Gradle plugin
      ```kotlin
        GradleRunner.create()
          .withProjectDir(projectDir)
          .withGradleVersion(gradleVersion)
          .withDebug(true)
          .forwardOutput()
      ```
        * Can not load the kotlin and android build-tool(The class in different class loader.)

          ```
          FAILURE: Build failed with an exception.
  
          * What went wrong:
            com/android/build/gradle/AppExtension
          > com.android.build.gradle.AppExtension
          * Try:
          Run with --info or --debug option to get more log output. Run with --scan to get full insights.
          * Exception is:
             java.lang.NoClassDefFoundError: com/android/build/gradle/AppExtension
             at com.github.jackchen.test.gradle.plugin.TemplatePlugin.apply(TemplatePlugin.kt:15)
             at com.github.jackchen.test.gradle.plugin.TemplatePlugin.apply(TemplatePlugin.kt:7)
          ...
          ```
        * We may need a simple repo for us to test.

## Links

[Ktlint Gradle Plugin on the Gradle Plugin Registry](https://plugins.gradle.org/plugin/org.jlleitschuh.gradle.ktlint)
