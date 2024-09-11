package com.eygraber.virtue.paths

import com.eygraber.virtue.config.VirtueAppInfo
import com.eygraber.virtue.di.scopes.AppSingleton
import dev.dirs.BaseDirectories
import dev.dirs.ProjectDirectories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import me.tatarka.inject.annotations.Provides
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
  private val baseDirs by lazy {
    BaseDirectories.get()
  }

  private val projectDirs by lazy {
    ProjectDirectories.from(
      appInfo.packageName,
      appInfo.orgName.orEmpty(),
      appInfo.name,
    )
  }

  private fun String.alsoMkDirs(): String = also {
    File(it).mkdirs()
  }

  override val userHomeDir: String by lazy {
    baseDirs.homeDir.alsoMkDirs()
  }

  override val cacheDir: String by lazy {
    baseDirs.cacheDir.alsoMkDirs()
  }

  override val projectCacheDir: String by lazy {
    projectDirs.cacheDir.alsoMkDirs()
  }

  override val configDir: String by lazy {
    baseDirs.configDir.alsoMkDirs()
  }

  override val projectConfigDir: String by lazy {
    projectDirs.configDir.alsoMkDirs()
  }

  override val dataDir: String by lazy {
    baseDirs.dataDir.alsoMkDirs()
  }

  override val projectDataDir: String by lazy {
    projectDirs.dataDir.alsoMkDirs()
  }

  override val noBackupDataDir: String by lazy {
    (projectDataLocalDir + File.separator + "noBackup").alsoMkDirs()
  }

  override val dataLocalDir: String by lazy {
    baseDirs.dataLocalDir.alsoMkDirs()
  }

  override val projectDataLocalDir: String by lazy {
    projectDirs.dataLocalDir.alsoMkDirs()
  }

  override val preferenceDir: String by lazy {
    baseDirs.preferenceDir.alsoMkDirs()
  }

  override val projectPreferenceDir: String by lazy {
    projectDirs.preferenceDir.alsoMkDirs()
  }
}

public suspend fun VirtuePaths.clearAllProjectData() {
  withContext(Dispatchers.IO) {
    File(projectCacheDir).deleteRecursively()
    File(projectConfigDir).deleteRecursively()
    File(projectDataDir).deleteRecursively()
    File(noBackupDataDir).deleteRecursively()
    File(projectDataLocalDir).deleteRecursively()
    File(projectPreferenceDir).deleteRecursively()
  }
}
