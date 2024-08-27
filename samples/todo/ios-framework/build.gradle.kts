import com.eygraber.conventions.kotlin.kmp.spm.registerAssembleXCFrameworkTasksFromFrameworks
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-detekt")
}

kotlin {
  kmpTargets(
    KmpTarget.Ios,
    ignoreDefaultTargets = true,
    project = project,
  )

  targets.withType<KotlinNativeTarget> {
    if(konanTarget.family.isAppleFamily) {
      binaries.framework {
        baseName = "TodoSample"
        binaryOption("bundleId", "com.eygraber.virtue.samples.todo.ios")
        export(projects.virtueApp)
      }
    }
  }

  project.registerAssembleXCFrameworkTasksFromFrameworks(
    frameworkName = "TodoSample",
  )

  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.samples.todo.shared)
        api(projects.virtueApp)
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
