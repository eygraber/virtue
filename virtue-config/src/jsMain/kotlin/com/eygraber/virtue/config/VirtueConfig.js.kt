package com.eygraber.virtue.config

public actual interface VirtueConfig {
  public actual val appName: String
}

public data class JsVirtueConfig(
  public override val appName: String,
  public val title: String = appName,
) : VirtueConfig
