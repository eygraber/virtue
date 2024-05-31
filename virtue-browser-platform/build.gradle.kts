plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
}

kotlin {
  kmpTargets(
    KmpTarget.Js,
    KmpTarget.WasmJs,
    project = project,
  )

  sourceSets {
    webMain {
      dependencies {
        implementation(libs.kotlinx.coroutines.core)
      }
    }
  }
}
