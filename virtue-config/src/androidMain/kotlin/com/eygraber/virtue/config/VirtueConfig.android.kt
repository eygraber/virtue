package com.eygraber.virtue.config

public actual interface VirtueConfig {
  public actual val appName: String
}

public data class AndroidVirtueConfig(
  public override val appName: String,
) : VirtueConfig
