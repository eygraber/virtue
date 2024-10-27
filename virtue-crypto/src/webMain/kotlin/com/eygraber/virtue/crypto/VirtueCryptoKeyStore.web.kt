package com.eygraber.virtue.crypto

import com.eygraber.virtue.browser.platform.IndexedDb
import com.eygraber.virtue.utils.runCatchingCoroutine
import com.juul.indexeddb.external.IDBKey
import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.AES
import dev.whyoleg.cryptography.algorithms.AES.Key
import dev.whyoleg.cryptography.algorithms.SHA256
import dev.whyoleg.cryptography.operations.AuthenticatedCipher
import dev.whyoleg.cryptography.providers.webcrypto.WebCrypto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

private val crypto by lazy {
  CryptographyProvider.WebCrypto
}

@Inject
public actual class VirtueCryptoKeyStore(
  private val database: IndexedDb,
) {
  @OptIn(ExperimentalEncodingApi::class)
  public actual suspend fun sha256Base64(value: String): String =
    Base64.Default.encode(
      crypto.get(SHA256).hasher().hash(value.encodeToByteArray()),
    )

  @OptIn(ExperimentalEncodingApi::class)
  public actual suspend fun getOrCreateAesGcmCipher(
    alias: String,
    shouldFailInInsecureEnvironments: Boolean,
    isDeviceUnlockRequired: Boolean,
  ): KeyStoreResult<AuthenticatedCipher> = withContext(Dispatchers.Default) {
    runCatchingCoroutine {
      when(
        val storedKey = database.readFromKeyValueStore(key(alias))
      ) {
        null -> KeyStoreResult.Success(
          crypto
            .get(AES.GCM)
            .keyGenerator(Key.Size.B256)
            .generateKey()
            .also { key ->
              val keyBase64 = Base64.Default.encode(key.encodeToByteArray(AES.Key.Format.RAW))

              database.writableKeyValueStore { store ->
                store.put(item = keyBase64, key = key(alias))
              }
            }
            .cipher(),
        )

        else -> KeyStoreResult.Success(
          crypto
            .get(AES.GCM)
            .keyDecoder()
            .decodeFromByteArray(AES.Key.Format.RAW, Base64.decode(storedKey.toString()))
            .cipher(),
        )
      }
    }.getOrElse(KeyStoreResult.Error::Generic)
  }

  private fun key(alias: String): IDBKey = IDBKey("com.eygraber.virtue.crypto.WebVirtueCryptoKeyStore.$alias")
}
