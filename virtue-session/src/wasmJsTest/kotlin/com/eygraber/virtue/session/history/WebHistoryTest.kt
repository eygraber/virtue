package com.eygraber.virtue.session.history

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.eygraber.virtue.back.press.dispatch.LocalOnBackPressedDispatcher
import com.eygraber.virtue.back.press.dispatch.OnBackPressedDispatcher
import com.eygraber.virtue.session.nav.VirtueRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class WebHistoryTest {
  @Serializable
  class Route : VirtueRoute {
    override fun display() = ""
  }

  @Test
  fun rememberSaveable_remembersTheStateOfHistory() = runComposeUiTest {
    var shouldCreateHistory by mutableStateOf(true)

    var history: History<Route>? = null
    var pushed = false

    setContent {
      CompositionLocalProvider(
        LocalOnBackPressedDispatcher provides OnBackPressedDispatcher(),
      ) {
        if(shouldCreateHistory) {
          history = rememberHistory(
            initialRoute = Route(),
            routeClass = Route::class,
          )

          if(!pushed) {
            history!!.push(
              Route(),
            )
            assertEquals(true, history?.canMoveBack, "Right after pushing it should be true")
            pushed = true
          }
        }
        else {
          history = null
        }
      }

      LaunchedEffect(Unit) {
        launch {
          delay(1_000)
          assertEquals(true, history?.canMoveBack, "Before disposing it should be true")
          shouldCreateHistory = false
          delay(1_000)
          assertEquals(null, history?.canMoveBack, "It should be null after disposing")
          // https://youtrack.jetbrains.com/issue/CMP-5909
          // shouldCreateHistory = true
          // delay(1_000)
          // assertEquals(true, history?.canMoveBack, "After disposing and recreating it should be true")
        }
      }
    }

    mainClock.advanceTimeBy(1000)
    mainClock.advanceTimeBy(1000)
    mainClock.advanceTimeBy(1000)
  }
}
