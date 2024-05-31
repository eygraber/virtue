import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-publish-maven-central")
  id("kotlinx-atomicfu")
  alias(libs.plugins.kotlinxSerialization)
}

android {
  namespace = "com.eygraber.virtue.session"
}

kotlin {
  defaultKmpTargets(
    project = project,
  )

  @OptIn(ExperimentalKotlinGradlePluginApi::class)
  applyDefaultHierarchyTemplate {
    common {
      group("notWeb") {
        withAndroidTarget()
        withJvm()
        withIos()
      }
    }
  }

  sourceSets {
    commonMain {
      dependencies {
        api(projects.virtueBackPressDispatch)
        api(projects.virtueDiComponents)
        api(projects.virtueDiScopes)
        api(projects.virtueNav)
        api(projects.virtueSessionState)
        api(projects.virtueTheme)
        implementation(projects.virtueThemeCompose)

        api(compose.material3)

        api(libs.kotlinx.coroutines.core)
        api(libs.kotlinInject.runtime)
        implementation(libs.kotlinx.serialization.core)
        implementation(libs.kotlinx.serialization.json)
        api(libs.uri)
        api(libs.vice.core)
        api(libs.vice.nav)
      }
    }

    webMain {
      dependencies {
        implementation(projects.virtueBrowserPlatform)
      }
    }

    webTest {
      dependencies {
        implementation(kotlin("test"))

        @OptIn(ExperimentalComposeLibrary::class)
        implementation(compose.uiTest)
      }
    }
  }
}
