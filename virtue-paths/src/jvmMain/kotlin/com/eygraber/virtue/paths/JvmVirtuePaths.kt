package com.eygraber.virtue.paths

import com.eygraber.virtue.config.VirtueAppInfo
import com.eygraber.virtue.di.scopes.AppSingleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import me.tatarka.inject.annotations.Provides
import net.harawata.appdirs.AppDirsFactory
import java.io.File

public actual interface VirtuePathsProvider {
  @Provides
  public fun JvmVirtuePaths.bind(): VirtuePaths = this
}

@AppSingleton
@Inject
public class JvmVirtuePaths(
  private val appInfo: VirtueAppInfo,
) : VirtuePaths {
  private val appDirs by lazy {
    AppDirsFactory.getInstance()
  }

  private fun String.alsoMkDirs(): String = also {
    File(it).mkdirs()
  }

  override val userHomeDir: String by lazy {
    System.getProperty("user.home").alsoMkDirs()
  }

  override val downloadsDir: String by lazy {
    appDirs
      .getUserDownloadsDir(
        appInfo.name,
        appInfo.versionName,
        appInfo.orgName,
      )
      .alsoMkDirs()
  }

  override val projectCacheDir: String by lazy {
    appDirs
      .getUserCacheDir(
        appInfo.name,
        appInfo.versionName,
        appInfo.orgName,
      )
      .alsoMkDirs()
  }

  override val projectConfigDir: String by lazy {
    appDirs
      .getUserConfigDir(
        appInfo.name,
        appInfo.versionName,
        appInfo.orgName,
      )
      .alsoMkDirs()
  }

  override val projectDataDir: String by lazy {
    appDirs
      .getUserDataDir(
        appInfo.name,
        appInfo.versionName,
        appInfo.orgName,
        true,
      )
      .alsoMkDirs()
  }

  override val noBackupDataDir: String by lazy {
    (projectDataLocalDir + File.separator + "noBackup").alsoMkDirs()
  }

  override val projectDataLocalDir: String by lazy {
    appDirs
      .getUserDataDir(
        appInfo.name,
        appInfo.versionName,
        appInfo.orgName,
        false,
      )
      .alsoMkDirs()
  }

  override val projectLogsDir: String by lazy {
    appDirs
      .getUserLogDir(
        appInfo.name,
        appInfo.versionName,
        appInfo.orgName,
      )
      .alsoMkDirs()
  }
}

public suspend fun VirtuePaths.clearAllProjectData(
  dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
  withContext(dispatcher) {
    File(projectCacheDir).deleteRecursively()
    File(projectConfigDir).deleteRecursively()
    File(projectDataDir).deleteRecursively()
    File(noBackupDataDir).deleteRecursively()
    File(projectDataLocalDir).deleteRecursively()
  }
}
