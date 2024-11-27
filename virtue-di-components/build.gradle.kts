plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.ksp)
}

android {
  namespace = "com.eygraber.virtue.di.components"
}

kotlin {
  defaultKmpTargets(
    project = project,
  )

  kspDependenciesForAllTargets {
    ksp(libs.kotlinInject.compiler)
  }

  sourceSets {
    androidMain.dependencies {
      api(projects.virtueAndroid)
    }

    commonMain.dependencies {
      api(projects.virtueConfig)
      api(projects.virtueDiScopes)
      api(projects.virtueInit)
      api(projects.virtuePaths)
      api(projects.virtueStorageKv)
      api(projects.virtueTheme)

      api(libs.kotlinInject.runtime)
    }

    wasmJsMain.dependencies {
      implementation(libs.kotlinx.wasm.browser)
    }
  }
}

ksp {
  arg("me.tatarka.inject.generateCompanionExtensions", "true")
}

// needed until https://github.com/google/ksp/issues/2243 is resolved
tasks.all {
  if(name.startsWith("kspKotlinIos")) {
    afterEvaluate {
      setOnlyIf { true }
    }
  }
}
