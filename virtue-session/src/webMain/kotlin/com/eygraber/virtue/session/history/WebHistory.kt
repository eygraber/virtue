package com.eygraber.virtue.session.history

import androidx.compose.runtime.saveable.Saver
import com.eygraber.virtue.back.press.dispatch.OnBackPressedDispatcher
import com.eygraber.virtue.browser.platform.BrowserPlatform
import com.eygraber.virtue.session.nav.VirtueRoute
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

internal class WebHistory<VR : VirtueRoute>(
  private val browserPlatform: BrowserPlatform,
  private val history: TimelineHistory<VR>,
  private val backPressDispatcher: OnBackPressedDispatcher,
  private var isDummyHeaderAdded: Boolean = false,
) : History<VR> {
  init {
    if(browserPlatform.currentHistoryEntryIndex == DUMMY_FIRST_HISTORY_INDEX) {
      // TODO: this is currently broken because history and navigation isn't persisted
      browserPlatform.go(1)
    }
    else {
      browserPlatform.replaceHistoryState(currentEntry.index, currentEntry.route.display())
    }
  }

  override val currentEntry: History.Entry<VR> get() = history.currentEntry
  override val currentEntryFlow: Flow<History.Entry<VR>> get() = history.currentEntryFlow
  override val canMoveBack: Boolean get() = history.canMoveBack
  override val canMoveForward: Boolean get() = history.canMoveForward

  override var isIgnoringPlatformChanges: Boolean
    get() = history.isIgnoringPlatformChanges
    set(value) {
      history.isIgnoringPlatformChanges = value
    }

  override fun get(index: Int): History.Entry<VR> = history[index]

  override fun push(route: VR): History.Entry<VR> =
    history.push(route).apply {
      browserPlatform.pushHistoryState(index, route.display())
    }

  override suspend fun clearForwardNavigation() {
    history.clearForwardNavigation()
    if(history.currentEntry.index == 0 && !isDummyHeaderAdded) {
      isDummyHeaderAdded = true
      browserPlatform.replaceHistoryState(DUMMY_FIRST_HISTORY_INDEX, "")
      browserPlatform.pushHistoryState(currentEntry.index, currentEntry.route.display())
    }
    else {
      isIgnoringPlatformChanges = true
      browserPlatform.go(-1)
      awaitChangeNoOp()
      browserPlatform.pushHistoryState(currentEntry.index, currentEntry.route.display())
    }
  }

  override fun replaceFirst(route: VR) {
    history.replaceFirst(route)
    browserPlatform.replaceHistoryState(0, route.display())
  }

  override fun move(delta: Int) {
    history.move(delta)
    browserPlatform.go(delta)
  }

  override fun updateTitle(title: String) {
    history.updateTitle(title)
    browserPlatform.documentTitle = title
  }

  override suspend fun awaitChangeNoOp() {
    browserPlatform.awaitPopstate()
  }

  override suspend fun awaitChange(): History.Change {
    val newIndex = browserPlatform.awaitPopstate()

    return when {
      isIgnoringPlatformChanges -> {
        isIgnoringPlatformChanges = false
        History.Change.Empty
      }

      newIndex == DUMMY_FIRST_HISTORY_INDEX -> {
        browserPlatform.go(-1)
        History.Change.Empty
      }

      newIndex == BrowserPlatform.BAD_POPSTATE -> History.Change.Empty

      // if someone is going to intercept the back press
      // then we move the browser back to where it was before the back press (the url might flash)
      // it will cause another popstate to fire but it is a no-op so we consume it
      isBackPress(newIndex) ->
        if(backPressDispatcher.hasEnabledCallbacks()) {
          browserPlatform.go(1)
          awaitChangeNoOp()

          History.Change.PendingAction(
            pending = {
              backPressDispatcher.onBackPressed()
            },
          )
        }
        else {
          history.move(-1)
          History.Change.Pop(1)
        }

      else -> handlePopStateEvent(history.currentEntry.index, newIndex)
    }
  }

  override fun toString(): String = history.toString()

  private fun handlePopStateEvent(
    currentIndex: Int,
    newIndex: Int,
  ): History.Change {
    val delta = newIndex - currentIndex
    if(delta != 0) history.move(delta)

    return when {
      delta < 0 -> History.Change.Pop(currentIndex - newIndex)

      delta > 0 -> History.Change.Navigate(currentIndex + 1..newIndex)

      else -> History.Change.Empty
    }
  }

  private fun isBackPress(newIndex: Int) =
    newIndex == history.currentEntry.index - 1

  internal companion object {
    fun <VR : VirtueRoute> Saver(
      routeClass: KClass<VR>,
      browserPlatform: BrowserPlatform,
      backPressDispatcher: OnBackPressedDispatcher,
    ) = Saver<WebHistory<VR>, String>(
      save = {
        val timeline = with(TimelineHistory.Saver(routeClass)) {
          save(it.history)
        }

        when {
          it.isDummyHeaderAdded -> "true$timeline"
          else -> timeline
        }
      },
      restore = { savedString ->
        val isDummyHeaderAdded = savedString.startsWith("true")
        with(TimelineHistory.Saver(routeClass)) {
          restore(savedString.removePrefix("true"))
        }?.let { timelineHistory ->
          WebHistory(
            browserPlatform = browserPlatform,
            history = timelineHistory,
            backPressDispatcher = backPressDispatcher,
            isDummyHeaderAdded = isDummyHeaderAdded,
          )
        }
      },
    )
  }
}

private const val DUMMY_FIRST_HISTORY_INDEX = -2
