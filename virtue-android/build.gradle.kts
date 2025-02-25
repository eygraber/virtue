plugins {
  kotlin("android")
  id("com.eygraber.conventions-kotlin-library")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
  alias(libs.plugins.dependencyAnalysis)
}

android {
  namespace = "com.eygraber.virtue.android"
}

dependencies {
  implementation(libs.kotlinInject.runtime)
}
