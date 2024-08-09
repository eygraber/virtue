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
import com.eygraber.virtue.session.nav.VirtueRoute
import kotlin.reflect.KClass

private const val SAVE_KEY = "com.eygraber.virtue.history.History"

private class HistorySaveableStateRegistry<VR : VirtueRoute>(
  private val routeClass: KClass<VR>,
  private val browserPlatform: BrowserPlatform,
  private val backPressDispatcher: OnBackPressedDispatcher,
) : SaveableStateRegistry by SaveableStateRegistry(
  restoredValues = browserPlatform.loadSessionState(SAVE_KEY)?.let { savedState ->
    mapOf(
      SAVE_KEY to listOf(
        with(
          WebHistory.Saver(
            routeClass = routeClass,
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
internal actual fun <VR : VirtueRoute> rememberHistory(
  initialRoute: VR,
  routeClass: KClass<VR>,
): History<VR> {
  val backPressDispatcher = checkNotNull(LocalOnBackPressedDispatcher.current) {
    "No OnBackPressedDispatcher was provided via LocalOnBackPressedDispatcher"
  }

  val browserPlatform = BrowserPlatform()

  val saveableStateRegistry = remember {
    HistorySaveableStateRegistry(
      routeClass = routeClass,
      browserPlatform = browserPlatform,
      backPressDispatcher = backPressDispatcher,
    )
  }

  var history: History<VR>? = null

  CompositionLocalProvider(
    LocalSaveableStateRegistry provides saveableStateRegistry,
  ) {
    history = rememberSaveable(
      saver = WebHistory.Saver(
        routeClass = routeClass,
        browserPlatform = browserPlatform,
        backPressDispatcher = backPressDispatcher,
      ),
      key = SAVE_KEY,
    ) {
      WebHistory(
        browserPlatform = browserPlatform,
        history = TimelineHistory(initialRoute),
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
