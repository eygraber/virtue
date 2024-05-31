package com.eygraber.virtue.session.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.compose.runtime.saveable.SaveableStateRegistry
import androidx.compose.runtime.saveable.rememberSaveable
import com.eygraber.virtue.back.press.dispatch.LocalOnBackPressedDispatcher
import com.eygraber.virtue.back.press.dispatch.OnBackPressedDispatcher
import com.eygraber.virtue.browser.platform.BrowserPlatform

private const val SAVE_KEY = "com.eygraber.virtue.history.History"

private class HistorySaveableStateRegistry(
  private val browserPlatform: BrowserPlatform,
  private val backPressDispatcher: OnBackPressedDispatcher,
) : SaveableStateRegistry by SaveableStateRegistry(
  restoredValues = browserPlatform.loadSessionState(SAVE_KEY)?.let { savedState ->
    mapOf(
      SAVE_KEY to listOf(
        with(
          WebHistory.Saver(
            browserPlatform = browserPlatform,
            backPressDispatcher = backPressDispatcher,
          ),
        ) {
          restore(savedState)
        },
      ),
    )
  },
  canBeSaved = { it is String },
) {
  fun save() {
    val map = performSave()
    map[SAVE_KEY]?.firstOrNull()?.let { history ->
      if(history is String) {
        browserPlatform.saveSessionState(SAVE_KEY, history)
      }
    }
  }
}

@Composable
internal actual fun rememberHistory(): History {
  val backPressDispatcher = checkNotNull(LocalOnBackPressedDispatcher.current) {
    "No OnBackPressedDispatcher was provided via LocalOnBackPressedDispatcher"
  }

  val browserPlatform = BrowserPlatform()

  val saveableStateRegistry = remember {
    HistorySaveableStateRegistry(
      browserPlatform = browserPlatform,
      backPressDispatcher = backPressDispatcher,
    )
  }

  var history: History? = null

  CompositionLocalProvider(
    LocalSaveableStateRegistry provides saveableStateRegistry,
  ) {
    history = rememberSaveable(
      saver = WebHistory.Saver(
        browserPlatform = browserPlatform,
        backPressDispatcher = backPressDispatcher,
      ),
      key = SAVE_KEY,
    ) {
      WebHistory(
        browserPlatform = browserPlatform,
        history = TimelineHistory(),
        backPressDispatcher = backPressDispatcher,
      )
    }
  }

  DisposableEffect(Unit) {
    onDispose {
      saveableStateRegistry.save()
    }
  }

  return history!!
}
