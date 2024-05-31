import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  kotlin("jvm")
  id("com.eygraber.conventions-kotlin-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-compose-jetbrains")
}

group = "samples-todo-desktop"

dependencies {
  implementation(projects.samples.todo.shared)
  implementation(projects.virtueApp)

  implementation(compose.desktop.currentOs)
}

compose {
  desktop.application.mainClass = "com.eygraber.virtue.samples.todo.desktop.TodoDesktopApplicationKt"
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
