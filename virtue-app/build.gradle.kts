plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-publish-maven-central")
  alias(libs.plugins.dependencyAnalysis)
}

android {
  namespace = "com.eygraber.virtue.app"
}

kotlin {
  defaultKmpTargets(
    project = project,
  )

  sourceSets {
    androidMain {
      dependencies {
        api(libs.androidx.activity)
        implementation(libs.androidx.activityCompose)
        implementation(libs.androidx.annotations)
        implementation(libs.androidx.lifecycle.common)
        implementation(libs.androidx.startup)
      }
    }

    commonMain {
      dependencies {
        api(projects.virtueConfig)
        api(projects.virtueDiComponents)
        api(projects.virtueSession)
        implementation(projects.virtueInit)
        implementation(projects.virtueTheme)
        implementation(projects.virtueThemeCompose)
        implementation(projects.virtueSessionState)

        implementation(compose.foundation)
        api(compose.material3)

        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.uri)
      }
    }
  }
}
