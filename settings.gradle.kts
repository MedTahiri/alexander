/*
 * This file was generated by the Gradle 'init' task.
 *
 * The settings file is used to specify which projects to include in your build.
 * For more detailed information on multi-project builds, please refer to https://docs.gradle.org/8.8/userguide/multi_project_builds.html in the Gradle documentation.
 */

rootProject.name = "alexander"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    plugins {
        kotlin("jvm") version "2.0.21"
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

include(":core", ":android", ":android:app", ":core:assets", ":backend", ":desktop", ":web")

project(":core").projectDir = file("core")
project(":android").projectDir = file("android")
project(":android:app").projectDir = file("android/app")
project(":core:assets").projectDir = file("core/assets")
project(":backend").projectDir = file("backend")
project(":desktop").projectDir = file("desktop")
project(":web").projectDir = file("web")
