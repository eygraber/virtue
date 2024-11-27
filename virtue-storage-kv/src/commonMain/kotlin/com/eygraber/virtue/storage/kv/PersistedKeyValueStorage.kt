package com.eygraber.virtue.storage.kv

import com.eygraber.virtue.paths.VirtuePaths
import com.eygraber.virtue.storage.kv.VirtueKeyValueStorage.Editable
import io.github.xxfast.kstore.KStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Serializable
internal data class KeyValue(
  val key: String,
  val value: String?,
)

@Inject
public expect class VirtueKStoreProvider {
  internal fun createStore(paths: VirtuePaths, name: String): KStore<List<KeyValue>>
}

@Inject
public class PersistedKeyValueStorage(
  paths: VirtuePaths,
  kStoreProvider: VirtueKStoreProvider,
  @Assisted private val name: String,
) : VirtueKeyValueStorage {
  private val store: KStore<List<KeyValue>> = kStoreProvider.createStore(paths, name)

  private val map = MutableStateFlow<Map<String, String?>>(sentinel)

  override val changes: Flow<Unit> = map.map {}

  override suspend fun contains(key: String): Boolean = awaitLoaded {
    key in map.value
  }

  override suspend fun getBoolean(key: String, default: Boolean): Boolean = awaitLoaded {
    map.value[key]?.toBooleanStrictOrNull() ?: default
  }

  override suspend fun getByte(key: String, default: Byte): Byte = awaitLoaded {
    map.value[key]?.toByteOrNull() ?: default
  }

  override suspend fun getDouble(key: String, default: Double): Double = awaitLoaded {
    map.value[key]?.toDoubleOrNull() ?: default
  }

  override suspend fun getFloat(key: String, default: Float): Float = awaitLoaded {
    map.value[key]?.toFloatOrNull() ?: default
  }

  override suspend fun getLong(key: String, default: Long): Long = awaitLoaded {
    map.value[key]?.toLongOrNull() ?: default
  }

  override suspend fun getInt(key: String, default: Int): Int = awaitLoaded {
    map.value[key]?.toIntOrNull() ?: default
  }

  override suspend fun getShort(key: String, default: Short): Short = awaitLoaded {
    map.value[key]?.toShortOrNull() ?: default
  }

  override suspend fun getString(key: String, default: String?): String? = awaitLoaded {
    map.value[key] ?: default
  }

  override fun edit(): Editable = object : EditableKeyValueStorage(map.value.toMutableMap()) {
    override suspend fun commit(storage: Map<String, String?>) {
      map.value = storage
      store.set(
        storage.map { KeyValue(it.key, it.value) },
      )
    }
  }

  private suspend inline fun <R> awaitLoaded(block: () -> R): R {
    if(map.value === sentinel) {
      map.value = store.get()?.associate { it.key to it.value }.orEmpty()
    }

    return block()
  }

  public companion object {
    private val sentinel = HashMap<String, String?>(0)
  }
}
