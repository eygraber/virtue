import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt")
  alias(libs.plugins.dependencyAnalysis)
}

group = "samples-auth-js"

kotlin {
  kmpTargets(
    KmpTarget.Js,
    project = project,
    binaryType = BinaryType.Executable,
    webOptions = KmpTarget.WebOptions(
      isNodeEnabled = false,
      isBrowserEnabled = true,
      moduleName = "auth-sample-js",
    ),
    ignoreDefaultTargets = true,
  )

  js(IR) {
    browser {
      commonWebpackConfig {
        outputFileName = "auth-sample-js.js"
        experiments += "topLevelAwait"
      }
    }
  }

  sourceSets {
    jsMain {
      dependencies {
        implementation(projects.samples.auth.shared)
        implementation(projects.virtueApp)

        implementation(compose.material3)
      }
    }
  }
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
