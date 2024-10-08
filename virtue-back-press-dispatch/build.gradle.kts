import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-publish-maven-central")
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.atomicfu)
}

android {
  namespace = "com.eygraber.virtue.back.press.dispatch"
}

kotlin {
  defaultKmpTargets(
    project = project,
  )

  @OptIn(ExperimentalKotlinGradlePluginApi::class)
  applyDefaultHierarchyTemplate {
    common {
      group("cmpWithoutDesktop") {
        withIos()
        withJs()
        withWasmJs()
      }
    }
  }

  sourceSets {
    androidMain {
      dependencies {
        implementation(libs.androidx.activityCompose)
      }
    }

    commonMain {
      dependencies {
        implementation(projects.virtueDiScopes)
        implementation(projects.virtuePlatform)

        implementation(compose.foundation)
        api(compose.runtime)
        implementation(compose.ui)

        implementation(libs.kotlinInject.runtimeKsp)
      }
    }
  }
}

configurations.all {
  resolutionStrategy.eachDependency {
    if(requested.group == "org.jetbrains.kotlinx" && requested.name == "atomicfu") {
      useVersion(libs.versions.atomicfuForCmp.get())
      because("https://youtrack.jetbrains.com/issue/CMP-5831")
    }
  }
}
