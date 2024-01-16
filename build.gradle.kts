// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.20"
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
    // Use GIT pre-commit gradle tasks in our app.
    id("org.jlleitschuh.gradle.ktlint-idea") version "10.3.0"
    `java-gradle-plugin`
    `maven-publish`
    id("com.vanniktech.maven.publish") version "0.19.0"
    id("com.github.gmazzo.buildconfig") version "5.3.5" apply false
}
repositories {
    maven("https://maven.aliyun.com/repository/central")
    maven("https://maven.aliyun.com/repository/public")
    google()
    mavenCentral()
    maven("https://plugins.gradle.org/m2/")
    maven("https://dl.bintray.com/jetbrains/kotlin-native-dependencies")
}
allprojects {
    plugins.withId("com.vanniktech.maven.publish") {
        mavenPublish {
            sonatypeHost = com.vanniktech.maven.publish.SonatypeHost.S01
        }
    }
}
