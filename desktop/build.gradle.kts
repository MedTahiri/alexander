import java.util.Locale

plugins {
    id("java")
    id("application")
}

group = "com.mohamed.tahiri"
version = "1.0"

application {
    applicationName = "desktop"
    mainClass = "com.mohamed.tahiri.Main"
    if (System.getProperty("os.name").lowercase(Locale.getDefault()).contains("mac")) {
        applicationDefaultJvmArgs = listOf("-XstartOnFirstThread")
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("org.jmonkeyengine:jme3-desktop:3.7.0-stable") {
        exclude(group = "org.jmonkeyengine", module = "jme3-android")
    }
    implementation("org.jmonkeyengine:jme3-lwjgl3:3.7.0-stable")
    if (!System.getProperty("os.name").lowercase(Locale.getDefault()).contains("mac")) {
        implementation("org.jmonkeyengine:jme3-awt-dialogs:3.7.0-stable")
    }
    implementation(project(":core"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
}
