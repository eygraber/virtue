package com.eygraber.virtue.init

import com.eygraber.virtue.crypto.KeyStoreResult
import com.eygraber.virtue.di.scopes.AppSingleton
import com.eygraber.virtue.storage.kv.DeviceKeyValueStorage
import com.eygraber.virtue.storage.kv.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

public data class VersionMigration(
  public val version: Int,
  public val versionName: String,
  public val description: String = "",
  public val migrate: suspend () -> Unit,
)

@AppSingleton
@Inject
public class VersionMigrationManager(
  private val deviceStorage: DeviceKeyValueStorage,
) {
  public sealed interface State {
    public data object Pending : State
    public data object Running : State
    public data object Finished : State

    public sealed interface Failed : State {
      public data class MigrationError(
        val migration: VersionMigration,
        val error: Throwable,
      ) : Failed

      public data class Error(
        val error: Throwable,
      ) : Failed

      public data object MigrationVersionsNotIncrementing : Failed
    }
  }

  public val stateFlow: StateFlow<State> = state

  public suspend fun migrateIfNeeded(
    migrations: List<VersionMigration>,
  ): State {
    withContext(Dispatchers.Default) {
      mutex.withLock {
        if(migrations.isValid()) {
          state.value = State.Running

          if(migrations.isNotEmpty()) {
            val persistedVersion = deviceStorage.getInt(MOST_RECENT_MIGRATED_VERSION, -1)
            val lastMigration = migrations.last()

            runCatching {
              migrations
                .filter { it.version in persistedVersion + 1..lastMigration.version }
                .forEach { migration ->
                  try {
                    migration.migrate()
                    deviceStorage.edit {
                      putInt(MOST_RECENT_MIGRATED_VERSION, migration.version)
                    }
                  }
                  catch(t: Throwable) {
                    state.value = State.Failed.MigrationError(
                      migration = migration,
                      error = t,
                    )

                    throw t
                  }
                }
            }.getOrElse { error ->
              if(state.value !is KeyStoreResult.Error) {
                state.value = State.Failed.Error(error = error)
              }
            }
          }

          state.value = State.Finished
        }
        else {
          state.value = State.Failed.MigrationVersionsNotIncrementing
        }
      }
    }

    return state.value
  }

  private fun List<VersionMigration>.isValid(): Boolean {
    zipWithNext { a, b ->
      if(b.version <= a.version) {
        return false
      }
    }

    return true
  }

  public companion object {
    private const val MOST_RECENT_MIGRATED_VERSION = "com.eygraber.virtue.init.MOST_RECENT_MIGRATED_VERSION"

    private val state = MutableStateFlow<State>(State.Pending)
    private val mutex = Mutex(locked = false)
  }
}
