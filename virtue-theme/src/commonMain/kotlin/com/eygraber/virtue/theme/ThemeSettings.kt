package com.eygraber.virtue.theme

import com.eygraber.virtue.di.scopes.AppSingleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject
import kotlin.concurrent.Volatile

@AppSingleton
@Inject
public class ThemeSettings(
  private val storage: ThemeSettingStorage,
) {
  private val mutableSetting = MutableStateFlow(ThemeSetting.System)
  public val setting: StateFlow<ThemeSetting> = mutableSetting

  @Volatile private var isLoaded: Boolean = false

  public suspend fun initialize(default: ThemeSetting = ThemeSetting.System) {
    mutableSetting.value = default
    val storedSetting = storage.load()
    if(storedSetting != null) {
      isLoaded = true
      mutableSetting.value = storedSetting
    }
  }

  public suspend fun update(setting: ThemeSetting) {
    isLoaded = true
    mutableSetting.value = setting
    storage.store(setting)
  }

  internal companion object {
    const val KEY = "com.eygraber.virtue.theme.ThemeSettings"
  }
}
