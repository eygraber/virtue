package com.eygraber.virtue.paths

public expect interface VirtuePathsProvider

public interface VirtuePaths {
  public val userHomeDir: String

  public val downloadsDir: String

  public val projectCacheDir: String

  public val projectConfigDir: String

  public val projectDataDir: String
  public val noBackupDataDir: String

  public val projectDataLocalDir: String

  public val projectLogsDir: String
}
