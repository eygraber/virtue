plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
  alias(libs.plugins.dependencyAnalysis)
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
      implementation(libs.kotlinx.coroutines.core)
    }

    jvmMain.dependencies {
      implementation(libs.jvmDirectories)
      implementation(libs.kotlinx.coroutines.core)
    }
  }
}
