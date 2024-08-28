plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.kotlinxSerialization)
}

android {
  namespace = "com.eygraber.virtue.storage.kv"
}

kotlin {
  defaultKmpTargets(
    project = project,
  )

  sourceSets {
    commonMain.dependencies {
      api(projects.virtueDiScopes)
      api(projects.virtuePaths)

      api(libs.kotlinInject.runtime)
      api(libs.kotlinx.coroutines.core)

      implementation(libs.kotlinx.serialization.json)

      implementation(libs.kstore)
    }

    androidMain.dependencies {
      implementation(libs.kstore.file)
    }

    iosMain.dependencies {
      implementation(libs.kstore.file)
    }

    jsMain.dependencies {
      implementation(libs.kstore.storage)
    }

    jvmMain.dependencies {
      implementation(libs.kstore.file)
    }

    wasmJsMain.dependencies {
      implementation(libs.kstore.storage)
    }
  }
}
