pluginManagement {
    repositories {
        // 优先使用 Google 官方仓库
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        maven(url = "https://maven.aliyun.com/repository/google") // 阿里云 Google 仓库镜像
        maven(url = "https://maven.aliyun.com/repository/central") // 阿里云中央仓库镜像
        mavenCentral() // Maven 官方中央仓库
        gradlePluginPortal() // Gradle 插件仓库
//        maven { url = uri("https://jitpack.io") }
        maven(url = "https://jitpack.io") // JitPack
    }

    plugins {
        id("com.google.devtools.ksp") version "2.0.21-1.0.25"
        id("com.google.dagger.hilt.android") version "2.57.2"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) // 强制在这里统一配置仓库
    repositories {
        google() // 官方 Google 仓库
        mavenCentral() // 官方 Maven 中央仓库
        flatDir {
            dirs("app/libs") // 指定本地文件夹
        }
        maven { url = uri("https://jitpack.io") }
        maven(url = "https://maven.aliyun.com/repository/google") // 阿里云 Google 仓库镜像
        maven(url = "https://maven.aliyun.com/repository/central") // 阿里云中央仓库镜像
        maven(url = "https://jitpack.io") // JitPack 仓库
    }
}

rootProject.name = "Albumio"
include(":app")


