plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-publish-maven-central")
  id("kotlinx-atomicfu")
}

android {
  namespace = "com.eygraber.virtue.back.press.dispatch"
}

kotlin {
  defaultKmpTargets(
    project = project,
  )

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
