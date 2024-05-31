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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class WebHistoryTest {
  @Test
  fun rememberSaveable_remembersTheStateOfHistory() = runComposeUiTest {
    var shouldCreateHistory by mutableStateOf(true)

    var history: History? = null
    var pushed = false

    setContent {
      CompositionLocalProvider(
        LocalOnBackPressedDispatcher provides OnBackPressedDispatcher(),
      ) {
        if(shouldCreateHistory) {
          history = rememberHistory()

          if(!pushed) {
            history!!.push()
            assertEquals(true, history?.canGoBack, "Right after pushing it should be true")
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
          assertEquals(true, history?.canGoBack, "Before disposing it should be true")
          shouldCreateHistory = false
          delay(1_000)
          assertEquals(null, history?.canGoBack, "It should be null after disposing")
          shouldCreateHistory = true
          delay(1_000)
          assertEquals(true, history?.canGoBack, "After disposing and recreating it should be true")
        }
      }
    }

    mainClock.advanceTimeBy(1000)
    mainClock.advanceTimeBy(1000)
    mainClock.advanceTimeBy(1000)
  }
}
