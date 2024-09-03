package com.eygraber.virtue.browser.platform

import Database
import ObjectStore
import Transaction
import WriteTransaction
import com.eygraber.virtue.utils.runCatchingCoroutine
import com.juul.indexeddb.external.IDBKey
import com.juul.indexeddb.external.IDBTransactionDurability
import com.juul.indexeddb.external.JsAny
import me.tatarka.inject.annotations.Inject
import openDatabase

@Inject
public class IndexedDb {
  private suspend fun open() =
    openDatabase(DATABASE_NAME, 1) { database, oldVersion, _ ->
      if(oldVersion < 1) {
        database.createObjectStore(KEY_VALUE_STORE)
      }
    }

  private suspend inline fun use(
    block: Database.() -> Unit,
    onError: (Throwable) -> Unit,
  ) {
    runCatchingCoroutine {
      val database = open()
      runCatchingCoroutine {
        database.block()
      }.getOrElse { error ->
        database.close()
        onError(error)
      }
    }.getOrElse(onError)
  }

  public suspend fun readableKeyValueStore(
    onError: (Throwable) -> Unit = {},
    durability: IDBTransactionDurability = IDBTransactionDurability.Default,
    block: suspend Transaction.(ObjectStore) -> Unit,
  ) {
    use(
      block = {
        transaction(
          store = KEY_VALUE_STORE,
          durability = durability,
        ) {
          block(objectStore(KEY_VALUE_STORE))
        }
      },
      onError = onError,
    )
  }

  public suspend fun readFromKeyValueStore(
    key: IDBKey,
    durability: IDBTransactionDurability = IDBTransactionDurability.Default,
  ): JsAny? {
    var ret: JsAny? = null
    readableKeyValueStore(
      durability = durability,
    ) { store ->
      ret = store.get(key)
    }

    return ret
  }

  public suspend fun writableKeyValueStore(
    onError: (Throwable) -> Unit = {},
    durability: IDBTransactionDurability = IDBTransactionDurability.Default,
    block: suspend WriteTransaction.(ObjectStore) -> Unit,
  ) {
    use(
      block = {
        writeTransaction(
          store = KEY_VALUE_STORE,
          durability = durability,
        ) {
          block(objectStore(KEY_VALUE_STORE))
        }
      },
      onError = onError,
    )
  }

  public companion object {
    private const val DATABASE_NAME = "com.eygraber.virtue"
    private const val KEY_VALUE_STORE = "com.eygraber.virtue.kv"
  }
}
