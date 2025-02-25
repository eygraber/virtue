plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
  alias(libs.plugins.dependencyAnalysis)
}

android {
  namespace = "com.eygraber.virtue.session.state"
}

kotlin {
  defaultKmpTargets(
    project = project,
  )

  sourceSets {
    commonMain.dependencies {
      api(projects.virtueDiScopes)

      api(libs.kotlinInject.runtime)
    }

    wasmJsMain.dependencies {
      implementation(libs.kotlinx.wasm.browser)
    }
  }
}
