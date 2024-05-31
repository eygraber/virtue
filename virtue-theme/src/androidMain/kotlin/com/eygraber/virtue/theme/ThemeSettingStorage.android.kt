package com.eygraber.virtue.theme

import android.content.SharedPreferences
import com.eygraber.virtue.di.scopes.AppSingleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@AppSingleton
@Inject
public actual class ThemeSettingStorage(
  private val sharedPreferencesProvider: (String) -> SharedPreferences,
  private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
  public actual suspend fun load(): ThemeSetting? =
    withContext(dispatcher) {
      sharedPreferencesProvider(ThemeSettings.KEY)
        .getString(KEY, null)
        ?.let { setting ->
          ThemeSetting.entries.find { it.name == setting }
        }
    }

  public actual suspend fun store(setting: ThemeSetting) {
    withContext(dispatcher) {
      sharedPreferencesProvider(ThemeSettings.KEY)
        .edit()
        .putString(KEY, setting.name)
        .apply()
    }
  }
}

private const val KEY = "setting"
