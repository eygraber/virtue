package com.eygraber.virtue.crypto

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.eygraber.virtue.paths.VirtuePaths
import com.eygraber.virtue.utils.runCatchingCoroutine
import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.AES
import dev.whyoleg.cryptography.algorithms.AES.Key
import dev.whyoleg.cryptography.algorithms.SHA256
import dev.whyoleg.cryptography.operations.AuthenticatedCipher
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
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
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
  private val cryptoDir = File(paths.projectConfigDir, "crypto").apply { mkdirs() }
  private val keyStoreFile = File(cryptoDir, "keystore.bks")
  private val keyStorePasswordFile = File(cryptoDir, "keyword")

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
        val keyStorePasswordAsync = loadKeyStorePassword(isDeviceUnlockRequired)
        val keyStore = loadOrCreateKeyStore(
          keyStorePasswordAsync = keyStorePasswordAsync,
        )

        when {
          keyStore == null -> KeyStoreResult.Error.InsecureEnvironment

          keyStore.isKeyEntry(alias) -> {
            if(keyStore.entryInstanceOf(alias, SecretKeyEntry::class.java)) {
              val key = keyStore.getKey(alias, alias.secretKeyPassword()) as SecretKey
              KeyStoreResult.Success(
                crypto.get(AES.GCM).keyDecoder().decodeFromByteArray(AES.Key.Format.RAW, key.encoded).cipher(),
              )
            }
            else {
              KeyStoreResult.Error.WrongKeyType
            }
          }

          else -> KeyStoreResult.Success(
            crypto
              .get(AES.GCM)
              .keyGenerator(Key.Size.B256)
              .generateKey()
              .also { key ->
                generateAndSaveKey(
                  keyStore = keyStore,
                  alias = alias,
                  key = key,
                  keyStorePasswordAsync = keyStorePasswordAsync,
                )
              }
              .cipher(),
          )
        }.also {
          Arrays.fill(keyStorePasswordAsync.await(), Char.MIN_VALUE)
        }
      }.getOrElse(KeyStoreResult.Error::Generic)
    }

  private suspend fun loadOrCreateKeyStore(
    keyStorePasswordAsync: Deferred<CharArray>,
  ): KeyStore? = withContext(Dispatchers.IO) {
    val keyStore = KeyStore.getInstance("BKS")

    val keyStorePassword = keyStorePasswordAsync.await()

    if(keyStoreFile.exists()) {
      keyStoreFile.inputStream().buffered().use { fis ->
        keyStore.load(fis, keyStorePassword)
      }
    }
    else {
      keyStore.load(null, keyStorePassword)
      keyStoreFile.outputStream().buffered().use { fis ->
        keyStore.store(fis, keyStorePassword)
      }
    }

    keyStore
  }

  private suspend fun generateAndSaveKey(
    keyStore: KeyStore,
    alias: String,
    key: AES.Key,
    keyStorePasswordAsync: Deferred<CharArray>,
  ) {
    val rawBytes = key.encodeToByteArray(AES.Key.Format.RAW)
    val secretKeyEntry = SecretKeyEntry(SecretKeySpec(rawBytes, "AES"))
    keyStore.setEntry(alias, secretKeyEntry, PasswordProtection(alias.secretKeyPassword()))

    val saved = saveKeyStore(
      keyStore = keyStore,
      keyStorePasswordAsync = keyStorePasswordAsync,
    )

    Arrays.fill(rawBytes, 0)

    check(saved) {
      "Saving the KeyStore failed because of an insecure environment"
    }
  }

  private suspend fun saveKeyStore(
    keyStore: KeyStore,
    keyStorePasswordAsync: Deferred<CharArray>,
  ): Boolean = keyStoreFile.outputStream().buffered().use { fis ->
    keyStore.store(fis, keyStorePasswordAsync.await())
    true
  }

  @OptIn(ExperimentalEncodingApi::class)
  private fun CoroutineScope.loadKeyStorePassword(
    isDeviceUnlockRequired: Boolean,
  ) = async {
    val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
      load(null)
    }

    val alias = "com.eygraber.virtue.crypto.AndroidCryptoKeyStore"
    val secretKey = if(keyStore.containsAlias(alias)) {
      keyStore.getKey(alias, null) as SecretKey
    }
    else {
      val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
      val keyGenParameterSpec = KeyGenParameterSpec.Builder(
        alias,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
      ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .setKeySize(256)
        .also {
          // there were a lot of issues related to using
          // setUnlockedDeviceRequired(true) before Android 15 (see docs)
          if(Build.VERSION.SDK_INT >= 35) {
            it.setUnlockedDeviceRequired(isDeviceUnlockRequired)
          }
        }
        .build()
      keyGenerator.init(keyGenParameterSpec)
      keyGenerator.generateKey()
    }

    val transformation = "AES/GCM/NoPadding"
    val tagLengthBit = 128
    val cipher = Cipher.getInstance(transformation)
    if(keyStorePasswordFile.exists()) {
      val decodedCipherText = Base64.Default.decode(keyStorePasswordFile.readBytes())

      val iv = ByteArray(decodedCipherText.last().toInt())
      System.arraycopy(decodedCipherText, 0, iv, 0, iv.size)
      val parameterSpec = GCMParameterSpec(tagLengthBit, iv)

      cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec)
      cipher.doFinal(decodedCipherText, iv.size, decodedCipherText.size - iv.size - 1).toCharArray().also {
        Arrays.fill(decodedCipherText, 0)
        Arrays.fill(iv, 0)
      }
    }
    else {
      cipher.init(Cipher.ENCRYPT_MODE, secretKey)

      val password = generateRandomPassword(length = 32)
      val passwordBytes = password.toByteArray()

      val cipherText = cipher.doFinal(passwordBytes)
      val iv = cipher.iv
      val cipherTextWithIv = ByteArray(iv.size + cipherText.size + 1)
      System.arraycopy(iv, 0, cipherTextWithIv, 0, iv.size)
      System.arraycopy(cipherText, 0, cipherTextWithIv, iv.size, cipherText.size)
      cipherTextWithIv[cipherTextWithIv.lastIndex] = iv.size.toByte()

      val base64Cipher = Base64.Default.encodeToByteArray(cipherTextWithIv)
      keyStorePasswordFile.writeBytes(base64Cipher)

      password.also {
        Arrays.fill(cipherText, 0)
        Arrays.fill(iv, 0)
        Arrays.fill(cipherTextWithIv, 0)
      }
    }
  }

  private fun String.secretKeyPassword() = reversed().toCharArray()
}

private fun CharArray.toByteArray() = ByteArray(size) { i -> this[i].code.toByte() }
private fun ByteArray.toCharArray() = CharArray(size) { i -> this[i].toInt().toChar() }
