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

group = "samples-todo-shared"

android {
  namespace = "com.eygraber.virtue.samples.todo.shared"
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
        implementation(projects.virtueSession)

        implementation(compose.material3)

        implementation(libs.kotlinx.coroutines.core)
        api(libs.kotlinx.serialization.json)
      }
    }
  }
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

// needed until https://github.com/google/ksp/issues/2243 is resolved
tasks.all {
  if(name.startsWith("kspKotlinIos")) {
    afterEvaluate {
      setOnlyIf { true }
    }
  }
}
