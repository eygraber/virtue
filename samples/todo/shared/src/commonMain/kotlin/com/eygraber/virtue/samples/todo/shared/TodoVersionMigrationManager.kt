package com.eygraber.virtue.samples.todo.shared

import com.eygraber.virtue.init.VersionMigration
import com.eygraber.virtue.init.VersionMigrationManager
import me.tatarka.inject.annotations.Inject

@Inject
class TodoVersionMigrationManager(
  private val versionMigrationManager: VersionMigrationManager,
) {
  private val migrations = listOf(
    VersionMigration(version = 1, versionName = "1.0.0") {
      println("Migrating to version 1")
    },
  )

  suspend fun migrateIfNeeded(): VersionMigrationManager.State =
    versionMigrationManager.migrateIfNeeded(
      migrations = migrations,
    )
}
