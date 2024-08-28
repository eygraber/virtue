plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-publish-maven-central")
  alias(libs.plugins.dependencyAnalysis)
}

android {
  namespace = "com.eygraber.virtue.app.compat"
}

kotlin {
  kmpTargets(
    KmpTarget.Android,
    ignoreDefaultTargets = true,
    project = project,
  )

  sourceSets {
    androidMain {
      dependencies {
        api(projects.virtueApp)
        api(projects.virtueConfig)
        api(projects.virtueDiComponents)
        api(projects.virtueSession)
        implementation(projects.virtueTheme)
        implementation(projects.virtueSessionState)
        implementation(projects.virtueThemeCompose)

        api(libs.androidx.appCompat)
        implementation(libs.androidx.activityCompose)
        implementation(libs.androidx.annotations)
        implementation(libs.androidx.lifecycle.common)

        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.uri)
      }
    }
  }
}
