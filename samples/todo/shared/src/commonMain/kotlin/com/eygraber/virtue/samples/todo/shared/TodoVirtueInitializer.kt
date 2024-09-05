package com.eygraber.virtue.samples.todo.shared

import com.eygraber.virtue.init.VirtueAppInitializer
import com.eygraber.virtue.storage.kv.DeviceKeyValueStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class TodoVirtueInitializer(
  private val versionMigrationManager: TodoVersionMigrationManager,
  override val deviceKeyValueStorage: DeviceKeyValueStorage,
) : VirtueAppInitializer() {
  override fun CoroutineScope.initialize() {
    launch {
      versionMigrationManager.migrateIfNeeded()
    }
  }
}
