package com.eygraber.virtue.config

public actual interface VirtueConfig {
  public actual val appName: String
}

public data class IosVirtueConfig(
  public override val appName: String,
) : VirtueConfig
