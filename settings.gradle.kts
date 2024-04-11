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
<<<<<<< HEAD
        maven{
=======
        maven {
            url = uri("$rootDir/maven-libs/")
        }
        maven { url = uri("https://jitpack.io") }
        maven {
>>>>>>> ed1daa202867d10ae8595b18b050adc9f4d51f0f
            setUrl("https://jitpack.io")
        }
    }
}

rootProject.name = "Euphonia"
include(":app")
