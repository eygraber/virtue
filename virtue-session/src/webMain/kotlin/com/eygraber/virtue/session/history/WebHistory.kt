package com.eygraber.virtue.session.history

import androidx.compose.runtime.saveable.Saver
import com.eygraber.uri.Url
import com.eygraber.virtue.back.press.dispatch.OnBackPressedDispatcher
import com.eygraber.virtue.browser.platform.BrowserPlatform

internal class WebHistory(
  private val browserPlatform: BrowserPlatform,
  private val history: TimelineHistory,
  private val backPressDispatcher: OnBackPressedDispatcher,
) : History {
  override var isEnabled: Boolean
    get() = history.isEnabled
    set(value) {
      history.isEnabled = value
    }

  override val currentEntry: History.Entry? get() = history.currentEntry
  override val canGoBack: Boolean get() = history.canGoBack
  override val canGoForward: Boolean get() = history.canGoForward

  override fun initialize() {
    history.initialize()
    browserPlatform.replaceHistoryState(0, "")
  }

  override fun push(): History.Entry =
    history.push().apply {
      browserPlatform.pushHistoryState(index)
    }

  override fun updateCurrent(display: String): History.Entry =
    history.updateCurrent(display).apply {
      browserPlatform.replaceHistoryState(index, display)
    }

  override fun move(delta: Int) {
    history.move(delta)
    browserPlatform.go(delta)
  }

  override suspend fun awaitChange(): History.Change =
    when(val newIndex = browserPlatform.awaitPopstate()) {
      BrowserPlatform.BAD_POPSTATE -> History.Change.Empty

      else -> if(isBackPress(newIndex)) {
        // if someone is going to intercept the back press (ignoring the session handler)
        // then we move the browser back to where it was before the back press (the url might flash)
        // it will cause another popstate to fire but this should be a no-op
        // since at that point the browser history and timeline history should be the same
        if(backPressDispatcher.hasEnabledCallbacks()) {
          browserPlatform.go(1)
        }

        backPressDispatcher.onBackPressed()

        History.Change.Empty
      }
      else {
        handlePopStateEvent(newIndex)
      }
    }

  override fun toString(): String = history.toString()

  private fun handlePopStateEvent(newIndex: Int): History.Change {
    val currentIndex = browserPlatform.currentHistoryEntryIndex
    val delta = newIndex - currentIndex
    return when {
      delta < 0 -> History.Change.Pop(currentIndex - newIndex)

      delta > 0 -> {
        val origin = browserPlatform.currentOrigin
        History.Change.Navigate(
          history
            .timelineDisplays
            .subList(currentIndex + 1, newIndex)
            .map { Url.parse("${origin}$it") },
        )
      }

      else -> History.Change.Empty
    }
  }

  private fun isBackPress(newIndex: Int): Boolean {
    val currentItem = history.currentEntry ?: return false
    return newIndex == currentItem.index - 1
  }

  internal companion object {
    fun Saver(
      browserPlatform: BrowserPlatform,
      backPressDispatcher: OnBackPressedDispatcher,
    ) = Saver<WebHistory, Any>(
      save = {
        with(TimelineHistory.Saver()) {
          save(it.history)
        }
      },
      restore = {
        when(it) {
          is WebHistory -> it

          is String -> with(TimelineHistory.Saver()) {
            restore(it)
          }?.let { timelineHistory ->
            WebHistory(
              browserPlatform = browserPlatform,
              history = timelineHistory,
              backPressDispatcher = backPressDispatcher,
            )
          }

          else -> null
        }
      },
    )
  }
}
