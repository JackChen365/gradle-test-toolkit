import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val pluginGroup = "com.test.plugin"
group = pluginGroup
version = rootProject.projectDir.resolve("VERSION_CURRENT.txt").readText().trim()

repositories {
    google()
    mavenCentral()
}

plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    `maven-publish`
    id("org.jlleitschuh.gradle.ktlint")
}

gradlePlugin {
    plugins {
        register("test-plugin") {
            id = "com.test.plugin"
            displayName = "TemplatePlugin"
            description = "This is a plugin template plugin."
            implementationClass = "com.github.jackchen.test.gradle.plugin.TemplatePlugin"
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

configurations.compileOnly.configure { isCanBeResolved = true }
tasks.withType<PluginUnderTestMetadata>().configureEach {
    pluginClasspath.from(configurations.compileOnly)
}

// Test tasks loods plugin from local maven repository
tasks.named("test").configure {
    dependsOn("publishToMavenLocal")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    compileOnly(gradleApi())
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.android.gradle.plugin)
    compileOnly(kotlin("stdlib-jdk8"))

    testImplementation(gradleTestKit())
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotlin.reflect)
    testImplementation(project(":test-toolkit"))
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
    reporters { reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML) }
}