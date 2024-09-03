package com.eygraber.virtue.storage.kv

import com.eygraber.virtue.crypto.KeyStoreResult
import com.eygraber.virtue.crypto.VirtueCryptoKeyStore
import com.eygraber.virtue.storage.kv.VirtueKeyValueStorage.Editable
import dev.whyoleg.cryptography.operations.cipher.AuthenticatedCipher
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

internal class EncryptedEditableKeyValueStorage(
  private val target: Editable,
  private val keyStore: VirtueCryptoKeyStore,
  private val name: String,
) : Editable {
  override suspend fun contains(key: String): Boolean = target.contains(hash(key))

  override suspend fun getBoolean(key: String, default: Boolean): Boolean = withCipher {
    target.getString(hash(key), null)?.let { decrypt(it) }
  }?.toBooleanStrictOrNull() ?: default

  override suspend fun getByte(key: String, default: Byte): Byte = withCipher {
    target.getString(hash(key), null)?.let { decrypt(it) }
  }?.toByteOrNull() ?: default

  override suspend fun getDouble(key: String, default: Double): Double = withCipher {
    target.getString(hash(key), null)?.let { decrypt(it) }
  }?.toDoubleOrNull() ?: default

  override suspend fun getFloat(key: String, default: Float): Float = withCipher {
    target.getString(hash(key), null)?.let { decrypt(it) }
  }?.toFloatOrNull() ?: default

  override suspend fun getLong(key: String, default: Long): Long = withCipher {
    target.getString(hash(key), null)?.let { decrypt(it) }
  }?.toLongOrNull() ?: default

  override suspend fun getInt(key: String, default: Int): Int = withCipher {
    target.getString(hash(key), null)?.let { decrypt(it) }
  }?.toIntOrNull() ?: default

  override suspend fun getShort(key: String, default: Short): Short = withCipher {
    target.getString(hash(key), null)?.let { decrypt(it) }
  }?.toShortOrNull() ?: default

  override suspend fun getString(key: String, default: String?): String? = withCipher {
    target.getString(hash(key), null)?.let { decrypt(it) }
  } ?: default

  override suspend fun putBoolean(key: String, value: Boolean): Editable = apply {
    withCipher {
      target.putString(hash(key), encrypt(value.toString()))
    }
  }

  override suspend fun putByte(key: String, value: Byte): Editable = apply {
    withCipher {
      target.putString(hash(key), encrypt(value.toString()))
    }
  }

  override suspend fun putDouble(key: String, value: Double): Editable = apply {
    withCipher {
      target.putString(hash(key), encrypt(value.toString()))
    }
  }

  override suspend fun putFloat(key: String, value: Float): Editable = apply {
    withCipher {
      target.putString(hash(key), encrypt(value.toString()))
    }
  }

  override suspend fun putLong(key: String, value: Long): Editable = apply {
    withCipher {
      target.putString(hash(key), encrypt(value.toString()))
    }
  }

  override suspend fun putInt(key: String, value: Int): Editable = apply {
    withCipher {
      target.putString(hash(key), encrypt(value.toString()))
    }
  }

  override suspend fun putShort(key: String, value: Short): Editable = apply {
    withCipher {
      target.putString(hash(key), encrypt(value.toString()))
    }
  }

  override suspend fun putString(key: String, value: String?): Editable = apply {
    withCipher {
      target.putString(hash(key), value?.let { encrypt(it) })
    }
  }

  override suspend fun remove(key: String): Editable = this.apply {
    withCipher {
      target.remove(hash(key))
    }
  }

  override fun clear(): Editable = this.apply {
    target.clear()
  }

  override suspend fun commit() {
    target.commit()
  }

  private suspend inline fun <R> withCipher(block: AuthenticatedCipher.() -> R): R? =
    when(val result = keyStore.getOrCreateAesGcmCipher(name)) {
      is KeyStoreResult.Success -> result.value.block()
      else -> null
    }

  private suspend fun hash(value: String): String = keyStore.sha256Base64(value)

  @OptIn(ExperimentalEncodingApi::class)
  private suspend fun AuthenticatedCipher.encrypt(value: String): String =
    Base64.Default.encode(encrypt(value.encodeToByteArray()))

  @OptIn(ExperimentalEncodingApi::class)
  private suspend fun AuthenticatedCipher.decrypt(value: String?): String? =
    value?.let { decrypt(Base64.Default.decode(it)).decodeToString() }
}
