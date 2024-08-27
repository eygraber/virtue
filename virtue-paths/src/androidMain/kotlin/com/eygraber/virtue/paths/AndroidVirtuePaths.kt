package com.eygraber.virtue.paths

import com.eygraber.virtue.android.AppContext
import com.eygraber.virtue.di.scopes.AppSingleton
import me.tatarka.inject.annotations.Inject
import me.tatarka.inject.annotations.Provides

public actual interface VirtuePathsProvider {
  @Provides
  public fun AndroidVirtuePaths.bind(): VirtuePaths = this
}

@AppSingleton
@Inject
public class AndroidVirtuePaths(
  private val context: AppContext,
) : VirtuePaths {
  override val userHomeDir: String by lazy {
    context.dataDir.absolutePath
  }

  override val cacheDir: String by lazy {
    context.cacheDir.absolutePath
  }

  override val projectCacheDir: String by lazy {
    context.cacheDir.absolutePath
  }

  override val configDir: String by lazy {
    context.filesDir.absolutePath
  }

  override val projectConfigDir: String by lazy {
    context.filesDir.absolutePath
  }

  override val dataDir: String by lazy {
    context.filesDir.absolutePath
  }

  override val projectDataDir: String by lazy {
    context.filesDir.absolutePath
  }

  override val noBackupDataDir: String by lazy {
    context.noBackupFilesDir.absolutePath
  }

  override val dataLocalDir: String by lazy {
    context.filesDir.absolutePath
  }

  override val projectDataLocalDir: String by lazy {
    context.filesDir.absolutePath
  }

  override val preferenceDir: String by lazy {
    context.filesDir.absolutePath
  }

  override val projectPreferenceDir: String by lazy {
    context.filesDir.absolutePath
  }
}
