package com.eygraber.virtue.paths

public expect interface VirtuePathsProvider

public interface VirtuePaths {
  public val userHomeDir: String

  public val cacheDir: String
  public val projectCacheDir: String

  public val configDir: String
  public val projectConfigDir: String

  public val dataDir: String
  public val projectDataDir: String
  public val noBackupDataDir: String

  public val dataLocalDir: String
  public val projectDataLocalDir: String

  public val preferenceDir: String
  public val projectPreferenceDir: String
}
