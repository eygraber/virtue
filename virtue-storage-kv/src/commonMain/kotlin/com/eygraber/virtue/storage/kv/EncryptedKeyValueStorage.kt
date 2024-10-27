package com.eygraber.virtue.storage.kv

import com.eygraber.virtue.crypto.KeyStoreResult
import com.eygraber.virtue.crypto.VirtueCryptoKeyStore
import dev.whyoleg.cryptography.operations.AuthenticatedCipher
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Inject
public class EncryptedKeyValueStorage(
  storage: (String) -> PersistedKeyValueStorage,
  private val keyStore: VirtueCryptoKeyStore,
  @Assisted private val name: String,
) : VirtueKeyValueStorage {
  private val storage = storage(name)

  override val changes: Flow<Unit> get() = storage.changes

  override suspend fun contains(key: String): Boolean = storage.contains(hash(key))

  override suspend fun getBoolean(key: String, default: Boolean): Boolean = withCipher {
    storage.getString(hash(key), null)?.let { decrypt(it) }
  }?.toBooleanStrictOrNull() ?: default

  override suspend fun getByte(key: String, default: Byte): Byte = withCipher {
    storage.getString(hash(key), null)?.let { decrypt(it) }
  }?.toByteOrNull() ?: default

  override suspend fun getDouble(key: String, default: Double): Double = withCipher {
    storage.getString(hash(key), null)?.let { decrypt(it) }
  }?.toDoubleOrNull() ?: default

  override suspend fun getFloat(key: String, default: Float): Float = withCipher {
    storage.getString(hash(key), null)?.let { decrypt(it) }
  }?.toFloatOrNull() ?: default

  override suspend fun getLong(key: String, default: Long): Long = withCipher {
    storage.getString(hash(key), null)?.let { decrypt(it) }
  }?.toLongOrNull() ?: default

  override suspend fun getInt(key: String, default: Int): Int = withCipher {
    storage.getString(hash(key), null)?.let { decrypt(it) }
  }?.toIntOrNull() ?: default

  override suspend fun getShort(key: String, default: Short): Short = withCipher {
    storage.getString(hash(key), null)?.let { decrypt(it) }
  }?.toShortOrNull() ?: default

  override suspend fun getString(key: String, default: String?): String? = withCipher {
    storage.getString(hash(key), null)?.let { decrypt(it) }
  } ?: default

  override fun edit(): VirtueKeyValueStorage.Editable =
    EncryptedEditableKeyValueStorage(
      target = storage.edit(),
      keyStore = keyStore,
      name = name,
    )

  private suspend fun hash(value: String): String = keyStore.sha256Base64(value)

  private suspend inline fun <R> withCipher(block: AuthenticatedCipher.() -> R): R? =
    when(val result = keyStore.getOrCreateAesGcmCipher(name)) {
      is KeyStoreResult.Success -> result.value.block()
      else -> null
    }

  @OptIn(ExperimentalEncodingApi::class)
  private suspend fun AuthenticatedCipher.decrypt(value: String?): String? =
    value?.let { decrypt(Base64.Default.decode(it)).decodeToString() }
}
