package com.eygraber.virtue.platform

public sealed interface Os {
  public data object Linux : Os
  public data object Mac : Os
  public data object Windows : Os
  public data object Unknown : Os
}

public sealed interface Platform {
  public data class Android(
    val sdkVersion: Int,
  ) : Platform
  public data object Ios : Platform
  public data class Jvm(
    val os: Os,
  ) : Platform
  public sealed interface Web : Platform {
    public data object Js : Platform
    public data object Wasm : Platform
  }
}

public expect val CurrentPlatform: Platform

public val Platform.isMacos: Boolean get() = this is Platform.Jvm && os == Os.Mac
public val Platform.isMobile: Boolean get() = this is Platform.Android || this is Platform.Ios
