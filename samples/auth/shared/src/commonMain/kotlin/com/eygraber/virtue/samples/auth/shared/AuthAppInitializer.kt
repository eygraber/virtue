package com.eygraber.virtue.samples.auth.shared

import com.eygraber.virtue.auth.VirtueAuth
import com.eygraber.virtue.di.scopes.AppSingleton
import com.eygraber.virtue.init.VirtueAppInitializer
import com.eygraber.virtue.storage.kv.DeviceKeyValueStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@AppSingleton
@Inject
class AuthAppInitializer(
  override val deviceKeyValueStorage: DeviceKeyValueStorage,
  private val auth: VirtueAuth,
) : VirtueAppInitializer() {
  override fun CoroutineScope.initialize() {
    launch {
      auth.initialize()
    }
  }
}
