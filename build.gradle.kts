
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("org.jetbrains.kotlin.jvm") version "1.9.10" apply false
    id("com.android.library") version "8.2.0" apply false
    id("androidx.navigation.safeargs") version "2.5.0" apply false
}

repositories {
    maven {
        url = uri("$rootDir/maven-libs/")
    }
    maven { url = uri("https://jitpack.io") }
}
