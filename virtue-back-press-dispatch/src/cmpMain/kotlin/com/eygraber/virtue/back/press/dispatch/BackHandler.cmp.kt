package com.eygraber.virtue.back.press.dispatch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

@Composable
public fun WithBackPressDispatching(
  onBackPressedDispatcher: OnBackPressedDispatcher,
  content: @Composable () -> Unit,
) {
  CompositionLocalProvider(
    LocalOnBackPressedDispatcher provides onBackPressedDispatcher,
    content = content,
  )
}

@Composable
public actual fun BackHandler(enabled: Boolean, onBackPress: () -> Unit) {
  val currentOnBackPressed by rememberUpdatedState(onBackPress)
  val callback = remember {
    object : OnBackPressedCallback {
      override var isEnabled: Boolean = enabled

      override fun onBackPressed() {
        currentOnBackPressed()
      }
    }
  }

  // On every successful composition, update the callback with the `enabled` value
  SideEffect {
    callback.isEnabled = enabled
  }

  val backPressDispatcher = checkNotNull(LocalOnBackPressedDispatcher.current) {
    "No OnBackPressedDispatcher was provided via LocalOnBackPressedDispatcher"
  }

  DisposableEffect(backPressDispatcher) {
    backPressDispatcher.addCallback(callback)
    onDispose {
      backPressDispatcher.removeCallback(callback)
    }
  }
}
