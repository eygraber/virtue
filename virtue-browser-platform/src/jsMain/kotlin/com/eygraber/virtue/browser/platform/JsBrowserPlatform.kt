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
  JsBrowserPlatform(
    browserHistory = window.history,
    browserLocation = window.location,
    browserSessionStorage = window.sessionStorage,
    browserDocument = window.document,
    browserWindow = window,
  )

public class JsBrowserPlatform(
  private val browserHistory: org.w3c.dom.History,
  private val browserLocation: org.w3c.dom.Location,
  private val browserSessionStorage: Storage,
  private val browserDocument: org.w3c.dom.Document,
  private val browserWindow: org.w3c.dom.Window,
) : BrowserPlatform {
  override val currentHistoryEntryIndex: Int get() = browserHistory.state?.unsafeCast<JsHistoryState>()?.index ?: 0
  override val currentOrigin: String get() = browserLocation.origin

  override var documentTitle: String
    get() = browserDocument.title
    set(value) {
      browserDocument.title = value
    }

  override fun pushHistoryState(index: Int, display: String) {
    browserHistory.pushState(jsHistoryState(index), "", display)
  }

  override fun replaceHistoryState(index: Int, display: String) {
    browserHistory.replaceState(jsHistoryState(index), "", display)
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
          val state = (event as PopStateEvent).state?.unsafeCast<JsHistoryState>()
          cont.resume(state?.index ?: BAD_POPSTATE)
        }
      }

      browserWindow.addEventListener("popstate", listener)

      cont.invokeOnCancellation {
        browserWindow.removeEventListener("popstate", listener)
      }
    }
}

internal external interface JsHistoryState {
  val index: Int
}

@Suppress("UNUSED_PARAMETER")
internal fun jsHistoryState(index: Int): dynamic = js("({ index: index })")
