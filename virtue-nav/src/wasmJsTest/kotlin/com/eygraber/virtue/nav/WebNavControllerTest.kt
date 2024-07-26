package com.eygraber.virtue.nav

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.eygraber.virtue.back.press.dispatch.LocalOnBackPressedDispatcher
import com.eygraber.virtue.back.press.dispatch.OnBackPressedDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class WebNavControllerTest {
  @Ignore // CMP rememberNavController doesn't save and restore state yet
  @Test
  fun checkTheController() = runComposeUiTest {
    var shouldCreateController by mutableStateOf(true)

    var controller: NavHostController? = null
    var navigated = false

    setContent {
      CompositionLocalProvider(
        LocalLifecycleOwner provides object : LifecycleOwner {
          override val lifecycle: Lifecycle = LifecycleRegistry(this)
        },
        LocalOnBackPressedDispatcher provides OnBackPressedDispatcher(),
      ) {
        if(shouldCreateController) {
          controller = rememberVirtueNavController()

          NavHost(
            navController = controller!!,
            startDestination = "test",
          ) {
            composable(
              route = "test",
            ) {}

            composable(
              route = "test2",
            ) {}
          }

          if(!navigated) {
            controller!!.navigate("test2")
            assertEquals("test2", controller?.currentDestination?.route, "Right after pushing it should be test2")
            navigated = true
          }
        }
        else {
          controller = null
        }
      }

      LaunchedEffect(Unit) {
        launch {
          delay(1_000)
          assertEquals("test2", controller?.currentDestination?.route, "Before disposing it should be test2")
          shouldCreateController = false
          delay(1_000)
          assertEquals(null, controller?.currentDestination?.route, "It should be null after disposing")
          shouldCreateController = true
          delay(1_000)
          assertEquals(
            "test2",
            controller?.currentDestination?.route,
            "After disposing and recreating it should be true",
          )
        }
      }
    }

    mainClock.advanceTimeBy(1000)
    mainClock.advanceTimeBy(1000)
    mainClock.advanceTimeBy(1000)
  }
}
