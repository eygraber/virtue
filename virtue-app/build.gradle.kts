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
        api(libs.androidx.activityCompose)
      }
    }

    commonMain {
      dependencies {
        api(projects.virtueSession)
        implementation(projects.virtueThemeCompose)

        implementation(compose.foundation)
        api(compose.material3)
      }
    }
  }
}
