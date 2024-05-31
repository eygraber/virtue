package com.eygraber.virtue.platform

private val os by lazy {
  val osName = System.getProperty("os.name")
  when {
    osName == null -> Os.Unknown
    osName.contains("Windows") -> Os.Windows
    osName.contains("Linux") -> Os.Linux
    osName.contains("OS X") -> Os.Mac
    else -> Os.Unknown
  }
}

public actual val CurrentPlatform: Platform = Platform.Jvm(os = os)
