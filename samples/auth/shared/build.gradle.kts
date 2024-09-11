import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-compose-jetbrains")
  alias(libs.plugins.kotlinxSerialization)
  alias(libs.plugins.ksp)
  alias(libs.plugins.dependencyAnalysis)
}

group = "samples-auth-shared"

android {
  namespace = "com.eygraber.virtue.samples.auth.shared"
}

kotlin {
  defaultKmpTargets(
    project = project,
  )

  kspDependenciesForAllTargets {
    ksp(libs.kotlinInject.compiler)
  }

  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.virtueApp)
        implementation(projects.virtueAuth)
        implementation(projects.virtueSession)

        implementation(compose.components.resources)
        implementation(compose.material3)
        implementation(libs.compose.placeholder)

        implementation(libs.kotlinx.coroutines.core)
        api(libs.kotlinx.serialization.json)
        implementation(libs.vice.sources)
      }
    }
  }
}

compose.resources {
  packageOfResClass = "com.eygraber.virtue.samples.auth.shared"
}

gradleConventions {
  kotlin {
    explicitApiMode = ExplicitApiMode.Disabled
  }
}

dependencyAnalysis {
  issues {
    onAny {
      severity("ignore")
    }
  }
}

ksp {
  arg("me.tatarka.inject.generateCompanionExtensions", "true")
}
