pluginManagement {
    plugins {
        id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
    }
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "VconexionSAS"
include(":app")


rootProject.name = "VconexionSAS"
include(":app")

 