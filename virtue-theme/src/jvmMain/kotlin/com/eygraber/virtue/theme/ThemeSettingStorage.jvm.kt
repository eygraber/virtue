package com.eygraber.virtue.theme

import com.eygraber.virtue.di.scopes.AppSingleton
import com.eygraber.virtue.paths.VirtuePaths
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import java.io.File

@AppSingleton
@Inject
public actual class ThemeSettingStorage(
  private val paths: VirtuePaths,
  private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
  public actual suspend fun load(): ThemeSetting? =
    withContext(dispatcher) {
      val file = File(paths.projectConfigDir, FILENAME)
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
      File(paths.projectConfigDir, FILENAME).writeText(setting.name)
    }
  }
}

private const val FILENAME = "${ThemeSettings.KEY}.conf"
