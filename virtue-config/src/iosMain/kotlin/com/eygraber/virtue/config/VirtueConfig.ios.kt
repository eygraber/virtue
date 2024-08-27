package com.eygraber.virtue.config

public actual interface VirtueConfig {
  public actual val appInfo: VirtueAppInfo
}

public data class IosVirtueConfig(
  public override val appInfo: VirtueAppInfo,
) : VirtueConfig
