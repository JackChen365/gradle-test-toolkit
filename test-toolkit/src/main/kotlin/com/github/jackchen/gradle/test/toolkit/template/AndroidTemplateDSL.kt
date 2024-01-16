package com.github.jackchen.gradle.test.toolkit.template

import com.github.jackchen.gradle.test.toolkit.testdsl.TestProjectRunner

/**
 * The Android template is for us to generate an Android test project by DSL
 *
 * There are several methods to help us configure the Android test project
 * 1. [PackageSpecScope]
 * <p>
 *     `package` {
 *          name = "test-app"
 *          packageName = "com.android.test"
 *      }
 * </p>
 * 2. [BuildSpecScope]
 * <p>
 *     build {
 *          targetSdk = 31
 *          targetSdk = 31
 *          minSdk = 21
 *      }
 * </p>
 * 3. [GradlePropertyHandler]
 * <p>
 *     properties {
 *          property("android.useAndroidX=true")
 *     }
 * </p>
 * 4. [RepositoryHandler]
 * <p>
 *     repositories {
 *          repo("google()")
 *          repo("mavenCentral()")
 *      }
 * </p>
 * 5. [PluginSpecScope]
 * <p>
 *     plugins {
 *          id("com.android.application").version("4.2.0")
 *          id("org.jetbrains.kotlin.android").version("1.6.21")
 *     }
 * </p>
 * 6. [DependencyHandler]
 * <p>
 *     dependencies {
 *          implementation("androidx.core:core-ktx:1.7.0")
 *          implementation("androidx.appcompat:appcompat:1.4.1")
 *      }
 * </p>
 *
 * After applying this DSL closure we will generate a standard Android test project for you.
 *
 * We have two subclasses:
 * 1. [GroovyAndroidTemplateProject] use the traditional groovy script.
 * 2. [KotlinAndroidTemplateProject] use the module Kotlin script.
 *
 * Please check the TestClass: GroovyAndroidTemplateProjectTest and KotlinAndroidTemplateProjectTest to know how to use it.
 *
 * @param runner The test project runner. This is where the Gradle runner executes the Gradle task.
 */
open class AndroidTemplateDSL(private val runner: TestProjectRunner) {
    companion object {
        private const val APP_DEFAULT_NAMESPACE = "com.android.test"
        private const val APP_COMPILESDK_VALUE = 34
        private const val APP_MINSDK_VALUE = 21
        private const val APP_TARGETSDK_VALUE = 34
        private const val APP_JVM_VERSION = "11"
    }

    var packageInfo = PackageSpecScope().apply {
        name = "test-app"
        packageName = "com.android.test"
    }
    var buildInfo = BuildSpecScope()
    var pluginsBlock: String = ""
    var androidBlock: String = ""
    var dependencyBlock: String = ""
    var propertySet = mutableSetOf<String>().apply {
        add("android.useAndroidX=true")
    }
    var repositorySet = mutableSetOf<String>().apply {
        add("google()")
        add("mavenLocal()")
        add("mavenCentral()")
    }
    var pluginSet = mutableSetOf<PluginSpecScope.Plugin>().apply {
        add(PluginSpecScope.Plugin("com.android.application", runner.testVersions.supportedAndroidVersion))
        add(PluginSpecScope.Plugin("org.jetbrains.kotlin.android", runner.testVersions.supportedKotlinPluginVersion))
    }
    var dependencyList = mutableListOf<Dependency>()

    class PackageSpecScope {
        var name: String? = null
        var packageName: String = "com.android.test"
    }

    class BuildSpecScope {
        var namespace: String = APP_DEFAULT_NAMESPACE
        var compileSdk: Int = APP_COMPILESDK_VALUE
        var minSdk = APP_MINSDK_VALUE
        var targetSdk = APP_TARGETSDK_VALUE
        var jvmVersion = APP_JVM_VERSION

        var versionName = "1.0"
        var versionCode = 1
    }

    class PluginSpecScope {
        val plugins = mutableSetOf<Plugin>()

        fun id(id: String): PluginVersion {
            val plugin = Plugin(id)
            plugins.add(plugin)
            return PluginVersion(plugin)
        }

        fun testId(id: String): Plugin {
            return Plugin(id)
        }

        class PluginVersion(private val plugin: Plugin) {
            fun version(version: String) {
                plugin.version = version
            }
        }

        data class Plugin(val id: String, var version: String? = null)
    }

    class Dependency(
        val configurationName: String,
        val dependencyNotation: String
    )

    class DependencyHandler {
        val dependencyList = mutableListOf<Dependency>()

        fun implementation(dependencyNotation: String) {
            dependencyList.add(Dependency("implementation", "\"$dependencyNotation\""))
        }

        @Suppress("unused")
        fun files(vararg dependencyNotations: String) {
            dependencyNotations.forEach { dependencyNotation ->
                dependencyList.add(Dependency("implementation", "files(\"$dependencyNotation\")"))
            }
        }

        fun testImplementation(dependencyNotation: String) {
            dependencyList.add(Dependency("testImplementation", "\"$dependencyNotation\""))
        }
    }

    class GradlePropertyHandler {
        val propertySet = mutableSetOf<String>()

        fun property(property: String) {
            propertySet.add(property)
        }
    }

    class RepositoryHandler {
        val repositorySet = mutableSetOf<String>()
        fun repo(repo: String) {
            repositorySet.add(repo)
        }
    }

    fun `package`(closure: PackageSpecScope.() -> Unit) {
        val packageInfo = PackageSpecScope()
        closure(packageInfo)
        this.packageInfo = packageInfo
    }

    fun build(closure: BuildSpecScope.() -> Unit) {
        val buildInfo = BuildSpecScope()
        closure(buildInfo)
        this.buildInfo = buildInfo
    }

    fun pluginsBlock(block: () -> String) {
        pluginsBlock = block()
    }

    fun androidBlock(block: () -> String) {
        androidBlock = block()
    }

    fun dependencyBlock(block: () -> String) {
        dependencyBlock = block()
    }

    fun repositories(closure: RepositoryHandler.() -> Unit) {
        val repositoryHandler = RepositoryHandler()
        closure(repositoryHandler)
        this.repositorySet.addAll(repositoryHandler.repositorySet)
    }

    fun properties(closure: GradlePropertyHandler.() -> Unit) {
        val gradlePropertyHandler = GradlePropertyHandler()
        closure(gradlePropertyHandler)
        this.propertySet.addAll(gradlePropertyHandler.propertySet)
    }

    fun plugins(closure: PluginSpecScope.() -> Unit) {
        val pluginSpecScope = PluginSpecScope()
        closure(pluginSpecScope)
        this.pluginSet.addAll(pluginSpecScope.plugins)
    }

    fun dependencies(closure: DependencyHandler.() -> Unit) {
        val dependencyHandler = DependencyHandler()
        closure(dependencyHandler)
        this.dependencyList = dependencyHandler.dependencyList
    }
}