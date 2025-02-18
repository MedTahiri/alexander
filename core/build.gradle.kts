import java.util.Locale

plugins {
    id("java")
    id("application")
}

group = "com.mohamed.tahiri"
version = "1.0"

application {
    applicationName = "core"
    mainClass = "com.mohamed.tahiri.Main"
    if (System.getProperty("os.name").lowercase(Locale.getDefault()).contains("mac")) {
        applicationDefaultJvmArgs = listOf("-XstartOnFirstThread")
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

val jmeVer = "3.6.1-stable"

dependencies {
    implementation(libs.jme3.core)
    implementation(libs.jme3.jogg)
    implementation(libs.jme3.plugins)
    implementation(libs.monkeywrench)
    implementation("org.apache.commons:commons-csv:1.10.0")
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
