package com.eygraber.virtue.storage.kv

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
public class VirtueKeyValueStorageCleaner(
  private val deviceKeyValueStorage: DeviceKeyValueStorage,
  private val encryptedDeviceKeyValueStorage: EncryptedDeviceKeyValueStorage,
  private val userKeyValueStorage: UserKeyValueStorage,
  private val encryptedUserKeyValueStorage: EncryptedUserKeyValueStorage,
) {
  public suspend fun cleanStorage() {
    withContext(Dispatchers.Default) {
      deviceKeyValueStorage.edit { clear() }
      encryptedDeviceKeyValueStorage.edit { clear() }
      userKeyValueStorage.edit { clear() }
      encryptedUserKeyValueStorage.edit { clear() }
    }
  }
}
