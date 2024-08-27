package com.eygraber.virtue.config

public actual interface VirtueConfig {
  public actual val appInfo: VirtueAppInfo
}

public data class AndroidVirtueConfig(
  public override val appInfo: VirtueAppInfo,
) : VirtueConfig
