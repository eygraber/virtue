import com.eygraber.conventions.kotlin.KotlinFreeCompilerArg
import com.eygraber.conventions.tasks.deleteRootBuildDirWhenCleaning
import com.google.common.base.CaseFormat
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

buildscript {
  dependencies {
    classpath(libs.buildscript.android)
    classpath(libs.buildscript.androidCacheFix)
    classpath(libs.buildscript.atomicfu)
    classpath(libs.buildscript.compose.compiler)
    classpath(libs.buildscript.compose.jetbrains)
    classpath(libs.buildscript.detekt)
    classpath(libs.buildscript.dokka)
    classpath(libs.buildscript.kotlin)
    classpath(libs.buildscript.publish)
  }
}

plugins {
  base
  alias(libs.plugins.conventions)
  alias(libs.plugins.dependencyAnalysis)
}

deleteRootBuildDirWhenCleaning()

gradleConventionsDefaults {
  android {
    sdkVersions(
      compileSdk = libs.versions.android.sdk.compile,
      targetSdk = libs.versions.android.sdk.target,
      minSdk = libs.versions.android.sdk.min,
    )
  }

  detekt {
    plugins(libs.detektCompose)
    plugins(libs.detektEygraber.formatting)
    plugins(libs.detektEygraber.style)
  }

  kotlin {
    jvmTargetVersion = JvmTarget.JVM_11
    explicitApiMode = ExplicitApiMode.Strict
    freeCompilerArgs = setOf(KotlinFreeCompilerArg.AllowExpectActualClasses)
  }
}

gradleConventionsKmpDefaults {
  webOptions = KmpTarget.WebOptions(
    isNodeEnabled = false,
    isBrowserEnabled = true,
  )

  targets(
    KmpTarget.Android,
    KmpTarget.Ios,
    KmpTarget.Js,
    KmpTarget.Jvm,
    KmpTarget.WasmJs,
  )
}

dependencyAnalysis {
  issues {
    all {
      onAny {
        severity("fail")
        exclude("org.jetbrains.kotlin:kotlin-stdlib")
      }
    }
  }

  structure {
    // Adds the defined aliases in the version catalog to be used when printing advice and rewriting build scripts.
    val versionCatalogName = "libs"
    val versionCatalog = project.extensions.getByType<VersionCatalogsExtension>().named(versionCatalogName)
    versionCatalog.libraryAliases.forEach { alias ->
      map.put(versionCatalog.findLibrary(alias).get().get().toString(), "$versionCatalogName.$alias")
    }

    subprojects.forEach { subproject ->
      val projectAccessor = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, subproject.path.replace(':', '.'))
      map.put(subproject.path, "projects$projectAccessor")
    }
  }
}
