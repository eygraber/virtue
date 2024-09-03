package com.eygraber.virtue.storage.kv

import com.eygraber.virtue.di.scopes.AppSingleton
import me.tatarka.inject.annotations.Inject

@AppSingleton
@Inject
public class UserKeyValueStorage(
  storage: (String) -> PersistedKeyValueStorage,
) : VirtueKeyValueStorage by storage(KEY) {
  public companion object {
    internal const val KEY: String = "com.eygraber.virtue.storage.kv.user"
  }
}

@AppSingleton
@Inject
public class EncryptedUserKeyValueStorage(
  storage: (String) -> EncryptedKeyValueStorage,
) : VirtueKeyValueStorage by storage(KEY) {
  public companion object {
    internal const val KEY: String = "com.eygraber.virtue.storage.kv.user_encrypted"
  }
}

@AppSingleton
@Inject
public class DeviceKeyValueStorage(
  storage: (String) -> PersistedKeyValueStorage,
) : VirtueKeyValueStorage by storage(KEY) {
  public companion object {
    internal const val KEY: String = "com.eygraber.virtue.storage.kv.device"
  }
}

@AppSingleton
@Inject
public class EncryptedDeviceKeyValueStorage(
  storage: (String) -> EncryptedKeyValueStorage,
) : VirtueKeyValueStorage by storage(KEY) {
  public companion object {
    internal const val KEY: String = "com.eygraber.virtue.storage.kv.device_encrypted"
  }
}
