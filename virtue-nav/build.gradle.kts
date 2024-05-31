import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-publish-maven-central")
  alias(libs.plugins.kotlinxSerialization)
}

android {
  namespace = "com.eygraber.virtue.nav"
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
        withIos()
        withJvm()
      }
    }
  }

  sourceSets {
    commonMain {
      dependencies {
        api(projects.virtueBackPressDispatch)

        api(libs.uri)
        api(libs.vice.nav)
      }
    }

    webMain {
      dependencies {
        implementation(projects.virtueBrowserPlatform)
        implementation(libs.kotlinx.serialization.cbor)
        implementation(libs.kotlinx.serialization.json)
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
