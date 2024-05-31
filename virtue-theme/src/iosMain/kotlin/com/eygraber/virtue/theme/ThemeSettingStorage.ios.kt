package com.eygraber.virtue.theme

import com.eygraber.virtue.di.scopes.AppSingleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import platform.Foundation.NSUserDefaults
import platform.Foundation.setValue

@AppSingleton
@Inject
public actual class ThemeSettingStorage {
  private val userDefaults = NSUserDefaults.standardUserDefaults()

  public actual suspend fun load(): ThemeSetting? =
    userDefaults
      .stringForKey(ThemeSettings.KEY)
      ?.let(ThemeSetting::valueOf)

  public actual suspend fun store(setting: ThemeSetting) {
    userDefaults.setValue(setting.name, forKey = ThemeSettings.KEY)
    withContext(Dispatchers.IO) {
      userDefaults.synchronize()
    }
  }
}
