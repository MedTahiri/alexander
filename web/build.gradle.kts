
plugins {
    id("java")
    id("org.teavm") version "0.10.2"
    id("war")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jmonkeyengine:jme3-core:3.6.1-stable")
    implementation(teavm.libs.jsoApis)
}

teavm.js {
    addedToWebApp = true
    mainClass = "com.mohamed.tahiri.Main"
    targetFileName = "Main.js"
}

//tasks.jar {
//    manifest {
//        attributes(
//            "Main-Class" to "com.mohamed.tahiri.Main"
//        )
//    }
//}
