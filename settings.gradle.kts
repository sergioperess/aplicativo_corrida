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
        maven {
            url = uri("https://maven.pkg.github.com/seu-usuario/seu-repositorio")
            credentials {
                username = System.getenv("USERNAME")
                password = System.getenv("API_TOKEN")
            }
        }
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/sergioperess/aplicativo_corrida")
            credentials{
                username = System.getenv("USERNAME")
                password = System.getenv("API_TOKEN")
            }
        }
    }
}

rootProject.name = "Av1_avancada"
include(":app")
include(":FuncLibrary")
