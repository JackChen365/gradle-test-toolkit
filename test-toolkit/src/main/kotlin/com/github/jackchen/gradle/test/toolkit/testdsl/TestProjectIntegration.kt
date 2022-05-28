package com.github.jackchen.gradle.test.toolkit.testdsl

import org.gradle.testkit.runner.BuildResult
import java.io.File

/**
 * The essential test project integration class
 * This class uses the DSL class: [TestProject] to create our test project.
 * For example:
 * <p>
 *      gradleProject {
 *          sourceDir("com.test") {
 *              dir("dir/dir2") {
 *                  file("gradle.properties") {
 *                      """kotlin.code.style=official"""
 *                  }
 *              }
 *              file("Main.kt") {
 *                  """
 *                  fun main(){
 *                  println("Hello world")
 *                  }
 *                  """.trimIndent()
 *              }
 *          }
 *      }
 *  </p>
 * Will help us generate a test project like this:
 * <p>
 * build/tmp
 *      |-- src/main/kotlin/com/test/dir
 *          |-- dir/dir2
 *              |-- gradle.properties
 *          |-- Main.kt
 *      |-- settings.gradle (Based on your module list)
 * </p>
 */
@Suppress("unused")
abstract class TestProjectIntegration {

    abstract fun project(closure: TestProject.() -> Unit)

    open class TestProject(private val runner: TestProjectRunner) {
        val projectDir = runner.projectDir
        val moduleList = mutableListOf<TestModuleDSL>()

        init {
            if (!projectDir.exists()) {
                projectDir.mkdirs()
            }
        }

        fun build(
            vararg buildArguments: String,
            assertions: BuildResult.() -> Unit = {}
        ) {
            runner.build(buildArguments, assertions)
        }

        fun buildAndFail(
            vararg buildArguments: String,
            assertions: BuildResult.() -> Unit = {}
        ) {
            runner.buildAndFail(buildArguments, assertions)
        }

        fun module(name: String, testModule: TestModuleDSL.() -> Unit) {
            val module = TestModuleDSL(name, projectDir)
            moduleList.add(module)
            testModule(module)
        }

        fun dir(path: String, testDir: TestDirDSL.() -> Unit) {
            val currentDir = File(projectDir, path)
            if (!currentDir.exists()) {
                currentDir.mkdirs()
            }
            testDir(TestDirDSL(currentDir))
        }

        fun file(name: String, closure: () -> String) {
            val currentFile = File(projectDir, name)
            currentFile.writeText(closure())
        }

        fun sourceDir(packageName: String, testDir: TestDirDSL.() -> Unit) {
            val currentDir = File(projectDir, "src/main/kotlin/${packageName.replace('.', '/')}")
            if (!currentDir.exists()) {
                currentDir.mkdirs()
            }
            testDir(TestDirDSL(currentDir))
        }

        fun settingGradle(content: () -> String) = projectDir.resolve("settings.gradle").writeText(content())

        fun settingGradle(content: String) = projectDir.resolve("settings.gradle").writeText(content)

        fun settingGradleKts(content: () -> String) = projectDir.resolve("settings.gradle.kts").writeText(content())

        fun settingGradleKts(content: String) = projectDir.resolve("settings.gradle.kts").writeText(content)

        fun buildGradle(content: () -> String) = projectDir.resolve("build.gradle").writeText(content())

        fun buildGradle(content: String) = projectDir.resolve("build.gradle").writeText(content)

        fun buildGradleKts(content: () -> String) = projectDir.resolve("build.gradle.kts").writeText(content())

        fun buildGradleKts(content: String) = projectDir.resolve("build.gradle.kts").writeText(content)

        fun gradleProperties(content: () -> String) = projectDir.resolve("gradle.properties").writeText(content())

        fun gradleProperties(content: String) = projectDir.resolve("gradle.properties").writeText(content)

        override fun toString(): String {
            return "TestProject(projectDir=${projectDir.name}, moduleList=${moduleList.joinToString { it.name }})"
        }
    }

    class TestModuleDSL(val name: String, projectDir: File) : TestDirDSL(File(projectDir, name)) {
        val moduleDir = File(projectDir, name).also { if (!it.exists()) it.mkdir() }

        fun sourceDir(packageName: String?, testDir: TestDirDSL.() -> Unit) {
            val currentDir = File(moduleDir, "src/main/kotlin/${packageName?.replace('.', '/')}")
            if (!currentDir.exists()) {
                currentDir.mkdirs()
            }
            testDir(TestDirDSL(currentDir))
        }

        fun kotlinSourceDir(packageName: String?, testDir: TestDirDSL.() -> Unit) {
            val currentDir = File(moduleDir, "src/main/kotlin/${packageName?.replace('.', '/')}")
            if (!currentDir.exists()) {
                currentDir.mkdirs()
            }
            testDir(TestDirDSL(currentDir))
        }

        override fun toString(): String {
            return "TestModule(name='$name', currentDir=$moduleDir)"
        }
    }

    open class TestDirDSL(private val currentDir: File) {
        private val fileList = mutableListOf<File>()

        init {
            if (!currentDir.exists()) currentDir.mkdir()
        }

        fun dir(path: String, testDir: (TestDirDSL.() -> Unit)? = null) {
            val currentDir = File(currentDir, path)
            if (!currentDir.exists()) {
                currentDir.mkdirs()
            }
            testDir?.invoke(TestDirDSL(currentDir))
        }

        fun file(name: String, closure: () -> String) {
            val currentFile = currentDir.resolve(name)
            currentFile.writeText(closure())
            fileList.add(currentFile)
        }
    }
}