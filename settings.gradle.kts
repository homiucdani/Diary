pluginManagement {
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

rootProject.name = "Diary"
include(":app")
include(":core:ui")
include(":core:util")
include(":data:mongo")
include(":feature:auth")
include(":feature:home")
include(":feature:details")
