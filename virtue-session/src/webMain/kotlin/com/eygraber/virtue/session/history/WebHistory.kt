package com.eygraber.virtue.session.history

import androidx.compose.runtime.saveable.Saver
import com.eygraber.virtue.back.press.dispatch.OnBackPressedDispatcher
import com.eygraber.virtue.browser.platform.BrowserPlatform
import com.eygraber.virtue.session.nav.VirtueRoute
import kotlin.reflect.KClass

internal class WebHistory<VR : VirtueRoute>(
  private val browserPlatform: BrowserPlatform,
  private val history: TimelineHistory<VR>,
  private val backPressDispatcher: OnBackPressedDispatcher,
) : History<VR> {
  init {
    browserPlatform.replaceHistoryState(currentEntry.index, currentEntry.route.display())
  }

  override val currentEntry: History.Entry<VR> get() = history.currentEntry
  override val canMoveBack: Boolean get() = history.canMoveBack
  override val canMoveForward: Boolean get() = history.canMoveForward

  override fun get(index: Int): History.Entry<VR> = history[index]

  override fun push(route: VR): History.Entry<VR> =
    history.push(route).apply {
      browserPlatform.pushHistoryState(index, route.display())
    }

  override fun move(delta: Int): History.Change {
    history.move(delta)
    browserPlatform.go(delta)
    return History.Change.Empty
  }

  override suspend fun awaitChangeNoOp() {
    browserPlatform.awaitPopstate()
  }

  override suspend fun awaitChange(): History.Change {
    val currentIndex = browserPlatform.currentHistoryEntryIndex

    val newIndex = browserPlatform.awaitPopstate()
    return when {
      newIndex == BrowserPlatform.BAD_POPSTATE -> History.Change.Empty

      // if someone is going to intercept the back press
      // then we move the browser back to where it was before the back press (the url might flash)
      // it will cause another popstate to fire but this should be a no-op so we consume it
      // since at that point the browser history and timeline history should be the same
      isBackPress(newIndex) ->
        if(backPressDispatcher.hasEnabledCallbacks()) {
          browserPlatform.go(1)
          backPressDispatcher.onBackPressed()
          awaitChangeNoOp()
          History.Change.Empty
        }
        else {
          history.move(-1)
          History.Change.Pop(1)
        }

      else -> handlePopStateEvent(currentIndex, newIndex)
    }
  }

  override fun toString(): String = history.toString()

  private fun handlePopStateEvent(
    currentIndex: Int,
    newIndex: Int,
  ): History.Change {
    val delta = newIndex - currentIndex
    history.move(delta)
    return when {
      delta < 0 -> History.Change.Pop(currentIndex - newIndex)

      delta > 0 -> History.Change.Navigate(currentIndex + 1..newIndex)

      else -> History.Change.Empty
    }
  }

  private fun isBackPress(newIndex: Int): Boolean {
    val currentItem = history.currentEntry
    return newIndex == currentItem.index - 1
  }

  internal companion object {
    fun <VR : VirtueRoute> Saver(
      routeClass: KClass<VR>,
      browserPlatform: BrowserPlatform,
      backPressDispatcher: OnBackPressedDispatcher,
    ) = Saver<WebHistory<VR>, String>(
      save = {
        with(TimelineHistory.Saver(routeClass)) {
          save(it.history)
        }
      },
      restore = {
        with(TimelineHistory.Saver(routeClass)) {
          restore(it)
        }?.let { timelineHistory ->
          WebHistory(
            browserPlatform = browserPlatform,
            history = timelineHistory,
            backPressDispatcher = backPressDispatcher,
          )
        }
      },
    )
  }
}
