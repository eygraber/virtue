package com.eygraber.virtue.storage.kv

import com.eygraber.virtue.browser.platform.IndexedDb
import com.eygraber.virtue.paths.VirtuePaths
import com.juul.indexeddb.external.IDBKey
import io.github.xxfast.kstore.Codec
import io.github.xxfast.kstore.DefaultJson
import io.github.xxfast.kstore.KStore
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import me.tatarka.inject.annotations.Inject

@Inject
public actual class VirtueKStoreProvider(
  private val database: IndexedDb,
) {
  internal actual fun createStore(
    paths: VirtuePaths,
    name: String,
  ): KStore<List<KeyValue>> = KStore(
    default = null,
    enableCache = false,
    codec = IndexedDbCodec(
      key = name,
      database = database,
    ),
  )
}

private inline fun <reified T : @Serializable Any> IndexedDbCodec(
  key: String,
  json: Json = DefaultJson,
  database: IndexedDb,
): IndexedDbCodec<T> = IndexedDbCodec(
  key = key,
  json = json,
  serializer = json.serializersModule.serializer(),
  database = database,
)

private class IndexedDbCodec<T : @Serializable Any>(
  private val key: String,
  private val json: Json,
  private val serializer: KSerializer<T>,
  private val database: IndexedDb,
) : Codec<T> {
  override suspend fun encode(value: T?) {
    database.writableKeyValueStore { store ->
      when(value) {
        null -> store.delete(IDBKey(key))
        else -> store.put(json.encodeToString(serializer, value), IDBKey(key))
      }
    }
  }

  override suspend fun decode(): T? =
    runCatching {
      database.readFromKeyValueStore(
        key = IDBKey(key),
      )
    }.getOrNull()?.let {
      json.decodeFromString(serializer, it.toString())
    }
}
