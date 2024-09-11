package com.eygraber.virtue.init

import com.eygraber.virtue.di.scopes.AppSingleton
import com.eygraber.virtue.storage.kv.DeviceKeyValueStorage
import com.eygraber.virtue.storage.kv.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@AppSingleton
public abstract class VirtueAppInitializer {
  private val firstOpen = MutableStateFlow<Boolean?>(null)

  protected abstract val deviceKeyValueStorage: DeviceKeyValueStorage

  public suspend fun isFirstOpen(): Boolean = firstOpen.filterNotNull().first()

  private var initialized = false

  @OptIn(DelicateCoroutinesApi::class)
  public fun initialize() {
    if(initialized) return

    initialized = true
    if(firstOpen.value == null) {
      GlobalScope.launch {
        val isFirstOpen = deviceKeyValueStorage.getBoolean(FIRST_OPEN, true)
        firstOpen.value = isFirstOpen
        if(isFirstOpen) {
          deviceKeyValueStorage.edit {
            putBoolean(FIRST_OPEN, false)
          }
        }
      }
    }

    GlobalScope.initialize()
  }

  protected abstract fun CoroutineScope.initialize()

  @Inject
  public class NoOp(
    override val deviceKeyValueStorage: DeviceKeyValueStorage,
  ) : VirtueAppInitializer() {
    override fun CoroutineScope.initialize() {}
  }

  public companion object {
    private const val FIRST_OPEN = "com.eygraber.virtue.init.FIRST_OPEN"
  }
}
