plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-publish-maven-central")
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
      }
    }

    commonMain {
      dependencies {
        api(projects.virtueConfig)
        api(projects.virtueDiComponents)
        api(projects.virtueNav)
        api(projects.virtueSession)
        implementation(projects.virtueTheme)
        implementation(projects.virtueSessionState)
        implementation(projects.virtueThemeCompose)

        implementation(compose.foundation)
        api(compose.material3)

        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.uri)
      }
    }
  }
}
