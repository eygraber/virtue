package com.eygraber.virtue.paths

import com.eygraber.virtue.di.scopes.AppSingleton
import kotlinx.cinterop.ExperimentalForeignApi
import me.tatarka.inject.annotations.Inject
import me.tatarka.inject.annotations.Provides
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSNumber
import platform.Foundation.NSURL
import platform.Foundation.NSURLIsExcludedFromBackupKey
import platform.Foundation.NSUserDomainMask

public actual interface VirtuePathsProvider {
  @Provides
  public fun IosVirtuePaths.bind(): VirtuePaths = this
}

@AppSingleton
@Inject
public class IosVirtuePaths : VirtuePaths {
  private val fileManager by lazy {
    NSFileManager.defaultManager
  }

  private val documents by lazy {
    fileManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask).first() as NSURL
  }

  private val libraryAppSupport by lazy {
    fileManager.URLsForDirectory(NSApplicationSupportDirectory, NSUserDomainMask).first() as NSURL
  }

  override val userHomeDir: String by lazy {
    NSHomeDirectory()
  }

  override val cacheDir: String by lazy {
    (fileManager.URLsForDirectory(NSCachesDirectory, NSUserDomainMask).first() as NSURL).createDirectory().path!!
  }

  override val projectCacheDir: String by lazy {
    cacheDir
  }

  override val configDir: String by lazy {

    libraryAppSupport.URLByAppendingPathComponent("config", isDirectory = true)!!.createDirectory().path!!
  }

  override val projectConfigDir: String by lazy {
    configDir
  }

  public val documentDataDir: String by lazy {
    documents.URLByAppendingPathComponent("data", isDirectory = true)!!.createDirectory().path!!
  }

  override val dataDir: String by lazy {
    libraryAppSupport.URLByAppendingPathComponent("data", isDirectory = true)!!.createDirectory().path!!
  }

  override val projectDataDir: String by lazy {
    dataDir
  }

  @OptIn(ExperimentalForeignApi::class)
  override val noBackupDataDir: String by lazy {
    libraryAppSupport.URLByAppendingPathComponent("noBackup", isDirectory = true)!!.createDirectory().apply {
      setResourceValue(NSNumber(bool = true), forKey = NSURLIsExcludedFromBackupKey, error = null)
    }.path!!
  }

  @OptIn(ExperimentalForeignApi::class)
  public val documentsNoBackupDataDir: String by lazy {
    documents.URLByAppendingPathComponent("noBackup", isDirectory = true)!!.createDirectory().apply {
      setResourceValue(NSNumber(bool = true), forKey = NSURLIsExcludedFromBackupKey, error = null)
    }.path!!
  }

  override val dataLocalDir: String by lazy {
    dataDir
  }

  override val projectDataLocalDir: String by lazy {
    projectDataDir
  }

  override val preferenceDir: String by lazy {
    libraryAppSupport.URLByAppendingPathComponent("preference", isDirectory = true)!!.createDirectory().path!!
  }

  override val projectPreferenceDir: String by lazy {
    preferenceDir
  }

  @OptIn(ExperimentalForeignApi::class)
  private fun NSURL.createDirectory(): NSURL = apply {
    fileManager.createDirectoryAtPath(
      path = path!!,
      withIntermediateDirectories = true,
      attributes = null,
      error = null,
    )
  }
}
