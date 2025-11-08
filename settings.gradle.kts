pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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

rootProject.name = "GiphyChili"
include(
    ":app",
    ":core:common",
    ":core:network",
    ":core:connectivity",
    ":core:ui",
    ":domain:giphy",
    ":data:giphy",
    ":feature:search",
    ":feature:detail"
)

