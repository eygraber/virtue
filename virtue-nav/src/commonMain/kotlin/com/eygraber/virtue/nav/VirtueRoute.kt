package com.eygraber.virtue.nav

public interface VirtueRoute {
  public val display: String

  public companion object {
    public const val INTERNAL_SCHEME: String = "com.eygraber.virtue.nav://"
  }
}
