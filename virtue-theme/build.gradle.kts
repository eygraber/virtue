plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
  alias(libs.plugins.dependencyAnalysis)
}

android {
  namespace = "com.eygraber.virtue.theme"
}

kotlin {
  defaultKmpTargets(
    project = project,
  )

  sourceSets {
    commonMain.dependencies {
      api(projects.virtueDiScopes)

      api(libs.kotlinInject.runtime)
      api(libs.kotlinInject.runtimeKsp)
      api(libs.kotlinx.coroutines.core)
    }

    jvmMain.dependencies {
      implementation(projects.virtuePaths)
    }

    wasmJsMain.dependencies {
      implementation(libs.kotlinx.wasm.browser)
    }
  }
}
