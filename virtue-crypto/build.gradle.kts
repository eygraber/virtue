plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
  alias(libs.plugins.kotlinxSerialization)
  alias(libs.plugins.dependencyAnalysis)
}

android {
  namespace = "com.eygraber.virtue.crypto"
}

kotlin {
  defaultKmpTargets(
    project = project,
  )

  sourceSets {
    commonMain.dependencies {
      api(projects.virtueDiScopes)
      api(projects.virtuePaths)

      api(libs.cryptoKmp.core)
      api(libs.kotlinInject.runtime)
      api(libs.kotlinx.coroutines.core)

      implementation(projects.virtueUtils)
    }

    androidMain.dependencies {
      implementation(libs.cryptoKmp.jdk)
    }

    iosMain.dependencies {
      implementation(libs.cryptoKmp.openssl3)
    }

    jvmMain.dependencies {
      implementation(libs.cryptoKmp.jdk)
      implementation(libs.jvmCredentialSecureStorage)
    }

    webMain.dependencies {
      implementation(projects.virtueBrowserPlatform)

      implementation(libs.cryptoKmp.webcrypto)
    }
  }
}
