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
    id("com.github.gmazzo.buildconfig")
}

repositories {
    google()
    mavenCentral()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
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
    buildConfigField(String::class.java, "APP_VERSION", rootProject.extra["VERSION_NAME"].toString())
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