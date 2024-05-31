package com.eygraber.virtue.config

import java.io.File

public actual interface VirtueConfig {
  public actual val appName: String
  public val configDir: File
}

public data class DesktopVirtueConfig(
  public override val appName: String,
  public override val configDir: File,
) : VirtueConfig
