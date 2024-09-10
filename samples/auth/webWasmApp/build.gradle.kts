import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt")
  alias(libs.plugins.dependencyAnalysis)
}

group = "samples-auth-wasmjs"

kotlin {
  kmpTargets(
    KmpTarget.WasmJs,
    project = project,
    binaryType = BinaryType.Executable,
    webOptions = KmpTarget.WebOptions(
      isNodeEnabled = false,
      isBrowserEnabled = true,
      moduleName = "auth-sample-wasm",
    ),
    ignoreDefaultTargets = true,
  )
  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    browser {
      commonWebpackConfig {
        outputFileName = "auth-sample-wasm.js"
        experiments += "topLevelAwait"

        devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
          static = (static ?: mutableListOf()).apply {
            // Serve sources to debug inside browser
            add(project.rootDir.path)
            add(project.projectDir.path)
            add(File(project.rootDir, "samples/auth/").path)
          }
        }
      }
    }
  }

  sourceSets {
    wasmJsMain {
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
