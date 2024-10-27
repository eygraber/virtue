package com.eygraber.virtue.crypto

import dev.whyoleg.cryptography.operations.AuthenticatedCipher
import dev.whyoleg.cryptography.random.CryptographyRandom
import me.tatarka.inject.annotations.Inject

public sealed interface KeyStoreResult<out T> {
  public data class Success<T>(val value: T) : KeyStoreResult<T>

  public sealed interface Error : KeyStoreResult<Nothing> {
    public data object InsecureEnvironment : Error
    public data object WrongKeyType : Error
    public data class Generic(val error: Throwable) : Error
  }
}

@Inject
public expect class VirtueCryptoKeyStore {
  public suspend fun sha256Base64(value: String): String

  public suspend fun getOrCreateAesGcmCipher(
    alias: String,
    shouldFailInInsecureEnvironments: Boolean = false,
    isDeviceUnlockRequired: Boolean = false,
  ): KeyStoreResult<AuthenticatedCipher>
}

internal fun generateRandomPassword(length: Int): CharArray {
  val charPool = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#\$%^&*()-_=+<>?"
  return CharArray(length) {
    charPool[CryptographyRandom.nextInt(charPool.length)]
  }
}
