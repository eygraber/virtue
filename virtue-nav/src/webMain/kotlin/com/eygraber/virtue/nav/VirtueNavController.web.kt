package com.eygraber.virtue.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.compose.runtime.saveable.SaveableStateRegistry
import androidx.core.bundle.Bundle
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.Navigator
import androidx.navigation.compose.rememberNavController
import com.eygraber.virtue.browser.platform.BrowserPlatform

private const val SAVE_KEY = "com.eygraber.virtue.nav.NavControllerSaveableStateRegistry"

private class NavControllerSaveableStateRegistry(
  private val browserPlatform: BrowserPlatform,
) : SaveableStateRegistry by SaveableStateRegistry(
  restoredValues = browserPlatform.loadSessionState(SAVE_KEY)?.let { savedState ->
    mapOf(
      SAVE_KEY to listOf(
        BundleCodec.decodeFromString(savedState),
      ),
    )
  },
  canBeSaved = { it is Bundle },
) {
  fun save() {
    val map = performSave()
    map.values.firstOrNull()?.firstOrNull()?.let { bundle ->
      if(bundle is Bundle) {
        browserPlatform.saveSessionState(SAVE_KEY, BundleCodec.encodeToString(bundle))
      }
    }
  }
}

@Composable
public actual fun rememberVirtueNavController(
  vararg navigators: Navigator<out NavDestination>,
): NavHostController {
  val saveableStateRegistry = remember {
    NavControllerSaveableStateRegistry(
      browserPlatform = BrowserPlatform(),
    )
  }

  var navController: NavHostController? = null

  CompositionLocalProvider(
    LocalSaveableStateRegistry provides saveableStateRegistry,
  ) {
    navController = rememberNavController(*navigators)
  }

  DisposableEffect(Unit) {
    onDispose {
      saveableStateRegistry.save()
    }
  }

  return navController!!
}
