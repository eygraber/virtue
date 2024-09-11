plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
  alias(libs.plugins.kotlinxSerialization)
  alias(libs.plugins.dependencyAnalysis)
}

android {
  namespace = "com.eygraber.virtue.auth"
}

kotlin {
  defaultKmpTargets(
    project = project,
  )

  sourceSets {
    commonMain.dependencies {
      api(projects.virtueDiScopes)
      api(projects.virtueStorageKv)
      api(projects.virtueUtils)

      api(libs.kotlinInject.runtime)
      api(libs.kotlinx.coroutines.core)
      api(libs.kotlinx.datetime)
      implementation(libs.kotlinx.serialization.cbor)
    }
  }
}
