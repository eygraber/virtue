package com.eygraber.virtue.paths

import android.content.Context
import android.os.Environment
import com.eygraber.virtue.android.AppContext
import com.eygraber.virtue.di.scopes.AppSingleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import me.tatarka.inject.annotations.Provides
import java.io.File

public actual interface VirtuePathsProvider {
  @Provides
  public fun AndroidVirtuePaths.bind(): VirtuePaths = this
}

@AppSingleton
@Inject
public class AndroidVirtuePaths(
  @AppContext
  private val context: Context,
) : VirtuePaths {
  override val userHomeDir: String by lazy {
    context.dataDir.absolutePath
  }

  override val downloadsDir: String by lazy {
    context
      .getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
      ?.absolutePath
      ?: projectDataDir
  }

  override val projectCacheDir: String by lazy {
    context.cacheDir.absolutePath
  }

  override val projectConfigDir: String by lazy {
    context.filesDir.absolutePath
  }

  override val projectDataDir: String by lazy {
    context.filesDir.absolutePath
  }

  override val noBackupDataDir: String by lazy {
    context.noBackupFilesDir.absolutePath
  }

  override val projectDataLocalDir: String by lazy {
    context.filesDir.absolutePath
  }

  override val projectLogsDir: String by lazy {
    File(context.cacheDir, "logs").absolutePath
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
