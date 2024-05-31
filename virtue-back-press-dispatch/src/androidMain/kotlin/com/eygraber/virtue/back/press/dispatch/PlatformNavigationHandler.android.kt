package com.eygraber.virtue.back.press.dispatch

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isBackPressed
import androidx.compose.ui.input.pointer.isForwardPressed
import androidx.compose.ui.input.pointer.pointerInput

@Suppress("ModifierMissing")
@Composable
public actual fun PlatformNavigationHandler(
  onForwardPress: () -> Unit,
) {
  val updatedOnForwardPressed by rememberUpdatedState(onForwardPress)

  Box(
    modifier = Modifier.pointerInput(Unit) {
      awaitEachGesture {
        // handle Android mouse events
        val event = awaitPointerEvent()
        if(event.type == PointerEventType.Release) {
          when {
            event.buttons.isBackPressed -> Unit // Activity handles this
            event.buttons.isForwardPressed -> updatedOnForwardPressed()
          }
        }
      }
    },
  )
}
