package com.eygraber.virtue.paths

import me.tatarka.inject.annotations.Inject
import me.tatarka.inject.annotations.Provides

public actual interface VirtuePathsProvider {
  @Provides
  public fun WebVirtuePaths.bind(): VirtuePaths = this
}

@Inject
public class WebVirtuePaths : VirtuePaths {
  override val userHomeDir: String = "home"
  override val downloadsDir: String = "downloads"
  override val projectCacheDir: String = "cache"
  override val projectConfigDir: String = "config"
  override val projectDataDir: String = "data"
  override val noBackupDataDir: String = "data"
  override val projectDataLocalDir: String = "data"
  override val projectLogsDir: String = "logs"
}
