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
      println("Web - Updating $index with $display")
      browserPlatform.replaceHistoryState(index, display)
    }

  override fun move(delta: Int): History.Change {
    history.move(delta)
    browserPlatform.go(delta)
    return History.Change.Empty
  }

  override suspend fun awaitChange(): History.Change {
    val currentIndex = browserPlatform.currentHistoryEntryIndex

    return when(val newIndex = browserPlatform.awaitPopstate()) {
      BrowserPlatform.BAD_POPSTATE -> History.Change.Empty

      else -> if(isBackPress(newIndex)) {
        // if someone is going to intercept the back press (ignoring the session handler)
        // then we move the browser back to where it was before the back press (the url might flash)
        // it will cause another popstate to fire but this should be a no-op
        // since at that point the browser history and timeline history should be the same
        if(backPressDispatcher.hasEnabledCallbacks()) {
          browserPlatform.go(1)
          backPressDispatcher.onBackPressed()
          History.Change.Empty
        }
        else {
          history.move(-1)
          History.Change.Pop(1)
        }
      }
      else {
        handlePopStateEvent(currentIndex, newIndex)
      }
    }
  }

  override fun toString(): String = history.toString()

  private fun handlePopStateEvent(
    currentIndex: Int,
    newIndex: Int
  ): History.Change {
    val delta = newIndex - currentIndex
    history.move(delta)
    return when {
      delta < 0 -> History.Change.Pop(currentIndex - newIndex)

      delta > 0 -> {
        val origin = browserPlatform.currentOrigin
        println("Navigating forward from $currentIndex to $newIndex in ${history.timelineDisplays}")
        History.Change.Navigate(
          history
            .timelineDisplays
            .slice(currentIndex + 1..newIndex)
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
