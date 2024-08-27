package com.eygraber.virtue.config

public actual interface VirtueConfig {
  public actual val appInfo: VirtueAppInfo
}

public data class JsVirtueConfig(
  public override val appInfo: VirtueAppInfo,
  public val title: String = appInfo.name,
) : VirtueConfig
