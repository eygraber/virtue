import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  kotlin("jvm")
  id("com.eygraber.conventions-kotlin-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-compose-jetbrains")
  alias(libs.plugins.dependencyAnalysis)
}

group = "samples-auth-desktop"

dependencies {
  implementation(projects.samples.auth.shared)
  implementation(projects.virtueApp)

  implementation(compose.desktop.currentOs)
}

compose {
  desktop.application.mainClass = "com.eygraber.virtue.samples.auth.desktop.AuthDesktopApplicationKt"
}

dependencyAnalysis {
  issues {
    onAny {
      severity("ignore")
    }
  }
}

gradleConventions {
  kotlin {
    explicitApiMode = ExplicitApiMode.Disabled
  }
}
