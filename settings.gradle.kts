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
//
//        // 配置雷鸟私有Maven服务器
//        maven {
//            url = uri("http://172.16.46.52:9998/repository/maven-public/")
//            isAllowInsecureProtocol = true
//        }
    }
}

rootProject.name = "ARClient"
include(":app")
