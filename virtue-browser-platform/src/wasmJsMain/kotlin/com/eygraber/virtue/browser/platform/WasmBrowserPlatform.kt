package com.eygraber.virtue.browser.platform

import com.eygraber.virtue.browser.platform.BrowserPlatform.Companion.BAD_POPSTATE
import kotlinx.browser.window
import kotlinx.coroutines.suspendCancellableCoroutine
import org.w3c.dom.PopStateEvent
import org.w3c.dom.Storage
import org.w3c.dom.events.Event
import org.w3c.dom.get
import org.w3c.dom.set
import kotlin.coroutines.resume

public actual fun BrowserPlatform(): BrowserPlatform =
  WasmBrowserPlatform(
    browserHistory = window.history,
    browserLocation = window.location,
    browserSessionStorage = window.sessionStorage,
    browserWindow = window,
  )

public class WasmBrowserPlatform(
  private val browserHistory: org.w3c.dom.History,
  private val browserLocation: org.w3c.dom.Location,
  private val browserSessionStorage: Storage,
  private val browserWindow: org.w3c.dom.Window,
) : BrowserPlatform {
  override val currentHistoryEntryIndex: Int get() = browserHistory.state?.unsafeCast<WasmHistoryState>()?.index ?: 0
  override val currentOrigin: String get() = browserLocation.origin

  override fun pushHistoryState(index: Int) {
    browserHistory.pushState(wasmHistoryState(index), "", "")
  }

  override fun replaceHistoryState(index: Int, display: String) {
    browserHistory.pushState(wasmHistoryState(index), "", display)
  }

  override fun go(delta: Int) {
    browserHistory.go(delta)
  }

  override fun saveSessionState(key: String, value: String) {
    browserSessionStorage[key] = value
  }

  override fun loadSessionState(key: String): String? =
    browserSessionStorage[key]

  override suspend fun awaitPopstate(): Int =
    suspendCancellableCoroutine { cont ->
      var listener: ((Event) -> Unit)? = null
      listener = { event ->
        if(listener != null && cont.isActive) {
          browserWindow.removeEventListener("popstate", listener)
          // https://youtrack.jetbrains.com/issue/KT-64565
          listener = null
          val state = (event as PopStateEvent).state?.unsafeCast<WasmHistoryState>()
          cont.resume(state?.index ?: BAD_POPSTATE)
        }
      }

      browserWindow.addEventListener("popstate", listener)

      cont.invokeOnCancellation {
        browserWindow.removeEventListener("popstate", listener)
        // https://youtrack.jetbrains.com/issue/KT-64565
        listener = null
      }
    }
}

internal external interface WasmHistoryState : JsAny {
  val index: Int
}

@Suppress("UNUSED_PARAMETER")
internal fun wasmHistoryState(index: Int): JsAny = js("({ index: index })")
