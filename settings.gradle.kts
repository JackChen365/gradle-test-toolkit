pluginManagement {
    repositories {
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
        maven("https://dl.bintray.com/jetbrains/kotlin-native-dependencies")
    }
}

// enableFeaturePreview("VERSION_CATALOGS")
rootProject.name = "gradle-test-toolkit"
rootProject.buildFileName = "build.gradle.kts"
include(":test-toolkit", ":test-plugin")
