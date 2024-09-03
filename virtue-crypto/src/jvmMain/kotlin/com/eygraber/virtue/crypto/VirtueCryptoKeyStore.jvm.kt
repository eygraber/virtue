package com.eygraber.virtue.crypto

import com.eygraber.virtue.paths.VirtuePaths
import com.eygraber.virtue.utils.runCatchingCoroutine
import com.microsoft.credentialstorage.StorageProvider
import com.microsoft.credentialstorage.StorageProvider.SecureOption
import com.microsoft.credentialstorage.model.StoredToken
import com.microsoft.credentialstorage.model.StoredTokenType
import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.digest.SHA256
import dev.whyoleg.cryptography.algorithms.symmetric.AES
import dev.whyoleg.cryptography.operations.cipher.AuthenticatedCipher
import dev.whyoleg.cryptography.providers.jdk.JDK
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import java.io.File
import java.security.KeyStore
import java.security.KeyStore.PasswordProtection
import java.security.KeyStore.SecretKeyEntry
import java.util.Arrays
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

private val crypto by lazy {
  CryptographyProvider.JDK
}

@Inject
public actual class VirtueCryptoKeyStore(
  paths: VirtuePaths,
) {
  private val keystoreFile = File(paths.projectConfigDir, "keystore.pkcs12")

  @OptIn(ExperimentalEncodingApi::class)
  public actual suspend fun sha256Base64(value: String): String =
    Base64.Default.encode(
      crypto.get(SHA256).hasher().hash(value.encodeToByteArray()),
    )

  public actual suspend fun getOrCreateAesGcmCipher(
    alias: String,
    shouldFailInInsecureEnvironments: Boolean,
    isDeviceUnlockRequired: Boolean,
  ): KeyStoreResult<AuthenticatedCipher> =
    withContext(Dispatchers.IO) {
      runCatchingCoroutine {
        val keyStorePasswordAsync = loadKeyStorePassword()
        val keyStore = loadOrCreateKeyStore(
          keyStorePasswordAsync = keyStorePasswordAsync,
          shouldFailInInsecureEnvironments = shouldFailInInsecureEnvironments,
        )

        val secretKeyPassword by lazy {
          alias.secretKeyPassword()
        }

        when {
          keyStore == null -> KeyStoreResult.Error.InsecureEnvironment

          keyStore.isKeyEntry(alias) -> {
            if(keyStore.entryInstanceOf(alias, SecretKeyEntry::class.java)) {
              val key = keyStore.getKey(alias, secretKeyPassword) as SecretKey
              KeyStoreResult.Success(
                crypto.get(AES.GCM).keyDecoder().decodeFrom(AES.Key.Format.RAW, key.encoded).cipher(),
              )
            }
            else {
              KeyStoreResult.Error.WrongKeyType
            }
          }

          else -> KeyStoreResult.Success(
            crypto
              .get(AES.GCM)
              .keyGenerator()
              .generateKey()
              .also { key ->
                val rawBytes = key.encodeTo(AES.Key.Format.RAW)
                val secretKeyEntry = SecretKeyEntry(SecretKeySpec(rawBytes, "AES"))
                keyStore.setEntry(alias, secretKeyEntry, PasswordProtection(secretKeyPassword))

                val saved = saveKeyStore(
                  keyStore = keyStore,
                  keyStorePasswordAsync = keyStorePasswordAsync,
                  shouldFailInInsecureEnvironments = shouldFailInInsecureEnvironments,
                )

                Arrays.fill(rawBytes, 0)

                check(saved) {
                  "Saving the KeyStore failed because of an insecure environment"
                }
              }
              .cipher(),
          )
        }.also {
          keyStorePasswordAsync.await()?.let { Arrays.fill(it, Char.MIN_VALUE) }
          Arrays.fill(secretKeyPassword, Char.MIN_VALUE)
        }
      }.getOrElse(KeyStoreResult.Error::Generic)
    }

  private suspend fun loadOrCreateKeyStore(
    keyStorePasswordAsync: Deferred<CharArray?>,
    shouldFailInInsecureEnvironments: Boolean,
  ): KeyStore? = withContext(Dispatchers.IO) {
    val keyStore = KeyStore.getInstance("PKCS12")

    val keyStorePassword = keyStorePasswordAsync.await()

    if(keyStorePassword == null && shouldFailInInsecureEnvironments) {
      null
    }
    else {
      if(keystoreFile.exists()) {
        keystoreFile.inputStream().buffered().use { fis ->
          keyStore.load(fis, keyStorePassword)
        }
      }
      else {
        keyStore.load(null, keyStorePassword)
        keystoreFile.outputStream().buffered().use { fis ->
          keyStore.store(fis, keyStorePassword)
        }
      }

      keyStore
    }
  }

  private suspend fun saveKeyStore(
    keyStore: KeyStore,
    keyStorePasswordAsync: Deferred<CharArray?>,
    shouldFailInInsecureEnvironments: Boolean,
  ): Boolean {
    val keyStorePassword = keyStorePasswordAsync.await()

    return if(keyStorePassword == null && shouldFailInInsecureEnvironments) {
      false
    }
    else {
      keystoreFile.outputStream().buffered().use { fis ->
        keyStore.store(fis, keyStorePassword)
        true
      }
    }
  }

  private fun CoroutineScope.loadKeyStorePassword() = async {
    when(val provider = StorageProvider.getTokenStorage(true, SecureOption.REQUIRED)) {
      null -> null

      else -> when(val secret = provider.get("com.eygraber.virtue.crypto.JvmCryptoKeyStore")) {
        null -> generateRandomPassword(length = 32).takeIf { password ->
          provider.add(
            "com.eygraber.virtue.crypto.JvmCryptoKeyStore",
            StoredToken(password, StoredTokenType.PERSONAL),
          )
        }

        else -> secret.value
      }
    }
  }

  private fun String.secretKeyPassword() = reversed().toCharArray()
}
