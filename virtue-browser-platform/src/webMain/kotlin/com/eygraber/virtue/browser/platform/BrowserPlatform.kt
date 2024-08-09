package com.eygraber.virtue.browser.platform

public expect fun BrowserPlatform(): BrowserPlatform

public interface BrowserPlatform {
  public val currentHistoryEntryIndex: Int
  public val currentOrigin: String

  public fun pushHistoryState(index: Int, display: String)
  public fun replaceHistoryState(index: Int, display: String)
  public fun go(delta: Int)

  public fun saveSessionState(key: String, value: String)
  public fun loadSessionState(key: String): String?

  public suspend fun awaitPopstate(): Int

  public companion object {
    public const val BAD_POPSTATE: Int = -1
  }
}
