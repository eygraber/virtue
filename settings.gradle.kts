import com.eygraber.conventions.Env
import com.eygraber.conventions.repositories.addCommonRepositories

pluginManagement {
  repositories {
    google {
      content {
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("androidx.*")
      }
    }

    mavenCentral()

    maven("https://oss.sonatype.org/content/repositories/snapshots") {
      mavenContent {
        snapshotsOnly()
      }
    }

    maven("https://s01.oss.sonatype.org/content/repositories/snapshots") {
      mavenContent {
        snapshotsOnly()
      }
    }

    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") {
      content {
        includeGroupByRegex("org\\.jetbrains.*")
      }
    }

    gradlePluginPortal()
  }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  // comment this out for now because it doesn't work with KMP js
  // https://youtrack.jetbrains.com/issue/KT-55620/KJS-Gradle-plugin-doesnt-support-repositoriesMode
  // repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

  repositories {
    addCommonRepositories(
      includeMavenCentral = true,
      includeMavenCentralSnapshots = true,
      includeGoogle = true,
      includeJetbrainsComposeDev = true,
    )
  }
}

rootProject.name = "virtue"

plugins {
  id("com.eygraber.conventions.settings") version "0.0.77"
  id("com.gradle.develocity") version "3.18.1"
}

include(":samples:auth:androidApp")
include(":samples:auth:desktopApp")
include(":samples:auth:ios-framework")
include(":samples:auth:shared")
include(":samples:auth:webJsApp")
include(":samples:auth:webWasmApp")
include(":samples:todo:androidApp")
include(":samples:todo:desktopApp")
include(":samples:todo:ios-framework")
include(":samples:todo:shared")
include(":samples:todo:webJsApp")
include(":samples:todo:webWasmApp")
include(":virtue-android")
include(":virtue-app")
include(":virtue-app-compat")
include(":virtue-auth")
include(":virtue-back-press-dispatch")
include(":virtue-browser-platform")
include(":virtue-config")
include(":virtue-crypto")
include(":virtue-di-components")
include(":virtue-di-scopes")
include(":virtue-init")
include(":virtue-paths")
include(":virtue-platform")
include(":virtue-session")
include(":virtue-session-state")
include(":virtue-storage-kv")
include(":virtue-theme")
include(":virtue-theme-compose")
include(":virtue-utils")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

develocity {
  buildScan {
    termsOfUseUrl = "https://gradle.com/terms-of-service"
    publishing.onlyIf { Env.isCI }
    if(Env.isCI) {
      termsOfUseAgree = "yes"
    }
  }
}
