@file:OptIn(ExperimentalForeignApi::class)

package com.eygraber.virtue.crypto

import com.eygraber.virtue.utils.runCatchingCoroutine
import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.AES
import dev.whyoleg.cryptography.algorithms.AES.Key
import dev.whyoleg.cryptography.algorithms.SHA256
import dev.whyoleg.cryptography.operations.AuthenticatedCipher
import dev.whyoleg.cryptography.providers.openssl3.Openssl3
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import platform.CoreFoundation.CFDictionaryCreate
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreFoundation.kCFBooleanFalse
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSMutableData
import platform.Foundation.NSString
import platform.Foundation.appendBytes
import platform.Security.SecCopyErrorMessageString
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecItemNotFound
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlock
import platform.Security.kSecAttrAccessibleWhenUnlocked
import platform.Security.kSecAttrService
import platform.Security.kSecAttrSynchronizable
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import platform.darwin.OSStatus
import platform.posix.memcpy
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

private val crypto by lazy {
  CryptographyProvider.Openssl3
}

@Inject
public actual class VirtueCryptoKeyStore {
  @OptIn(ExperimentalEncodingApi::class)
  public actual suspend fun sha256Base64(value: String): String =
    Base64.Default.encode(
      crypto.get(SHA256).hasher().hash(value.encodeToByteArray()),
    )

  public actual suspend fun getOrCreateAesGcmCipher(
    alias: String,
    shouldFailInInsecureEnvironments: Boolean,
    isDeviceUnlockRequired: Boolean,
  ): KeyStoreResult<AuthenticatedCipher> = withContext(Dispatchers.IO) {
    runCatchingCoroutine {
      KeyStoreResult.Success(
        when(val storedKey = retrieveKey(alias)) {
          null ->
            crypto
              .get(AES.GCM)
              .keyGenerator(Key.Size.B256)
              .generateKey()
              .also { key ->
                storeKey(alias, key.encodeToByteArray(AES.Key.Format.RAW), isDeviceUnlockRequired)
              }
              .cipher()

          else ->
            crypto
              .get(AES.GCM)
              .keyDecoder()
              .decodeFromByteArray(AES.Key.Format.RAW, storedKey)
              .cipher()
        },
      )
    }.getOrElse(KeyStoreResult.Error::Generic)
  }

  private fun retrieveKey(alias: String): ByteArray? = cfRetain(alias) { cfAlias ->
    val cfValue = alloc<CFTypeRefVar>()

    val keychainQuery = cfDictionaryOf(
      mapOf(
        kSecClass to kSecClassGenericPassword,
        kSecAttrService to cfAlias,
        kSecAttrSynchronizable to kCFBooleanFalse,
        kSecReturnData to kCFBooleanTrue,
        kSecMatchLimit to kSecMatchLimitOne,
      ),
    )

    when(val status = SecItemCopyMatching(keychainQuery, cfValue.ptr)) {
      errSecSuccess -> (CFBridgingRelease(cfValue.value) as? NSData)?.toByteArray()
      errSecItemNotFound -> null
      else -> {
        status.reportError()
        null
      }
    }
  }

  private fun storeKey(
    alias: String,
    key: ByteArray,
    isDeviceUnlockRequired: Boolean,
  ) {
    val keyData = key.toNSData()

    cfRetain(alias, keyData) { cfAlias, cfValue ->
      val keychainQuery = cfDictionaryOf(
        mapOf(
          kSecClass to kSecClassGenericPassword,
          kSecAttrService to cfAlias,
          kSecAttrSynchronizable to kCFBooleanFalse,
          kSecAttrAccessible to when {
            isDeviceUnlockRequired -> kSecAttrAccessibleWhenUnlocked
            else -> kSecAttrAccessibleAfterFirstUnlock
          },
          kSecValueData to cfValue,
        ),
      )

      // delete any existing item
      SecItemDelete(keychainQuery)

      val status = SecItemAdd(keychainQuery, null)
      if(status != errSecSuccess) {
        status.reportError()
      }
    }
  }
}

private fun ByteArray.toNSData(): NSData = NSMutableData().apply {
  this@toNSData.usePinned {
    appendBytes(it.addressOf(0), size.toULong())
  }
}

private fun NSData.toByteArray(): ByteArray {
  val length = this.length.toInt()
  val byteArray = ByteArray(length)
  byteArray.usePinned {
    memcpy(it.addressOf(0), this.bytes, length.convert())
  }
  return byteArray
}

private fun MemScope.cfDictionaryOf(map: Map<CFStringRef?, CFTypeRef?>): CFDictionaryRef? {
  val size = map.size
  val keys = allocArrayOf(*map.keys.toTypedArray())
  val values = allocArrayOf(*map.values.toTypedArray())
  return CFDictionaryCreate(
    kCFAllocatorDefault,
    keys.reinterpret(),
    values.reinterpret(),
    size.convert(),
    null,
    null,
  )
}

private inline fun <T> cfRetain(value: Any?, block: MemScope.(CFTypeRef?) -> T): T = memScoped {
  val cfValue = CFBridgingRetain(value)
  return try {
    block(cfValue)
  }
  finally {
    CFBridgingRelease(cfValue)
  }
}

internal inline fun <T> cfRetain(value1: Any?, value2: Any?, block: MemScope.(CFTypeRef?, CFTypeRef?) -> T): T =
  memScoped {
    val cfValue1 = CFBridgingRetain(value1)
    val cfValue2 = CFBridgingRetain(value2)
    return try {
      block(cfValue1, cfValue2)
    }
    finally {
      CFBridgingRelease(cfValue1)
      CFBridgingRelease(cfValue2)
    }
  }

private fun OSStatus.reportError() {
  val cfMessage = SecCopyErrorMessageString(this, null)
  val nsMessage = CFBridgingRelease(cfMessage) as? NSString
  val message = nsMessage?.toKString() ?: "Unknown error"
  error("Keychain error $this: $message")
}

@Suppress("CAST_NEVER_SUCCEEDS")
internal fun NSString.toKString() = this as String
