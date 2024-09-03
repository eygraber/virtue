plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
  alias(libs.plugins.dependencyAnalysis)
}

kotlin {
  kmpTargets(
    KmpTarget.Js,
    KmpTarget.WasmJs,
    ignoreDefaultTargets = true,
    project = project,
  )

  sourceSets {
    webMain {
      dependencies {
        api(libs.indexedDb.core)
        api(libs.indexedDb.externals)
        implementation(libs.kotlinx.coroutines.core)

        api(libs.kotlinInject.runtime)
      }
    }
  }
}
