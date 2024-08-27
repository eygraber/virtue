plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
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
    androidMain {
      dependencies {
        api(projects.virtueAndroid)
      }
    }

    commonMain {
      dependencies {
        api(projects.virtueConfig)
        api(projects.virtueDiScopes)
        api(projects.virtuePaths)
        api(projects.virtueStorageKv)
        api(projects.virtueTheme)

        api(libs.kotlinInject.runtime)
      }
    }
  }
}

ksp {
  arg("me.tatarka.inject.generateCompanionExtensions", "true")
}
