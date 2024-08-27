plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
}

android {
  namespace = "com.eygraber.virtue.paths"
}

kotlin {
  defaultKmpTargets(
    project = project,
  )

  sourceSets {
    commonMain.dependencies {
      api(projects.virtueConfig)
      api(projects.virtueDiScopes)
      api(libs.kotlinInject.runtime)
    }

    androidMain.dependencies {
      api(projects.virtueAndroid)
    }

    jvmMain.dependencies {
      implementation(libs.jvmDirectories)
    }
  }
}
