import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.20")
    }
}

plugins {
    kotlin("jvm")
    `maven-publish`
    id("org.jlleitschuh.gradle.ktlint")
    id("com.vanniktech.maven.publish")
    id("com.github.gmazzo.buildconfig") version "3.1.0"
}

repositories {
    google()
    mavenCentral()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    implementation(gradleApi())
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.android.gradle.plugin)
    implementation(libs.junit.jupiter)
    implementation(kotlin("stdlib-jdk8"))
    implementation(libs.commons.io)
    implementation(gradleTestKit())
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotlin.reflect)
}

buildConfig {
    buildConfigField(
        "String",
        "APP_NAME",
        "\"${project.name}\""
    )
    buildConfigField(
        "String",
        "APP_VERSION",
        provider {
            val projectVersion = file("../gradle.properties").readLines().find { it.startsWith("VERSION_NAME") }?.substringAfterLast("=")
            "\"${projectVersion}\""
        }
    )
}

ktlint {
    version.set("0.45.2")
    debug.set(false)
    verbose.set(true)
    outputToConsole.set(true)
    outputColorName.set("RED")
    ignoreFailures.set(false)
    enableExperimentalRules.set(true)
    disabledRules.set(listOf("final-newline"))
    baseline.set(file("config/ktlint/baseline.xml"))
    reporters { reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML) }
}