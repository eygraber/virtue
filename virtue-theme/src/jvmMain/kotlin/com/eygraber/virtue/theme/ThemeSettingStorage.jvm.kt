package com.eygraber.virtue.theme

import com.eygraber.virtue.config.VirtueConfig
import com.eygraber.virtue.di.scopes.AppSingleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import java.io.File

@AppSingleton
@Inject
public actual class ThemeSettingStorage(
  private val config: VirtueConfig,
  private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
  public actual suspend fun load(): ThemeSetting? =
    withContext(dispatcher) {
      val file = File(config.configDir, FILENAME)
      if(file.exists() && file.canRead()) {
        val setting = file.readText().trimEnd()
        ThemeSetting.entries.find { it.name == setting }
      }
      else {
        null
      }
    }

  public actual suspend fun store(setting: ThemeSetting) {
    withContext(dispatcher) {
      File(config.configDir, FILENAME).writeText(setting.name)
    }
  }
}

private const val FILENAME = "${ThemeSettings.KEY}.conf"
