package com.eygraber.virtue.config

public data class VirtueAppInfo(
  public val versionCode: Long,
  public val versionName: String,
  public val packageName: String,
  public val name: String,
  public val orgName: String? = null,
)
