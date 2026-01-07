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
        // Xposed API 仓库
        maven { url = uri("https://api.xposed.info/") }
    }
}

rootProject.name = "TikTokSIMSpoof"
include(":app")
