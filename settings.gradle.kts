pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("$rootDir/maven-libs/")
        }
        maven { url = uri("https://jitpack.io") }
        maven {
            setUrl("https://jitpack.io")
        }
    }
}

rootProject.name = "Euphonia"
include(":app")
