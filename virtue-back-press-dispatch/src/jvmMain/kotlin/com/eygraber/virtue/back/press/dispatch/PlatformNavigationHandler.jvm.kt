package com.eygraber.virtue.back.press.dispatch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.eygraber.virtue.platform.CurrentPlatform
import com.eygraber.virtue.platform.Os
import com.eygraber.virtue.platform.Platform
import com.eygraber.virtue.platform.isMacos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.AWTEvent
import java.awt.Toolkit
import java.awt.event.AWTEventListener
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent

@Composable
public actual fun PlatformNavigationHandler(
  onForwardPress: () -> Unit,
) {
  val os = (CurrentPlatform as Platform.Jvm).os

  val toolkit = Toolkit.getDefaultToolkit()

  val scope = rememberCoroutineScope {
    Dispatchers.Main.immediate
  }

  val backPressDispatcher = checkNotNull(LocalOnBackPressedDispatcher.current) {
    "No OnBackPressedDispatcher was provided via LocalOnBackPressedDispatcher"
  }

  val awtKeyListener = AWTEventListener { event ->
    if(event is KeyEvent && event.id == KeyEvent.KEY_RELEASED && !event.isConsumed) {
      val mask = if(CurrentPlatform.isMacos) KeyEvent.META_DOWN_MASK else KeyEvent.CTRL_DOWN_MASK
      val action = when {
        event.modifiersEx and mask == mask -> when(event.keyCode) {
          KeyEvent.VK_LEFT -> backPressDispatcher::onBackPressed
          KeyEvent.VK_RIGHT -> onForwardPress
          else -> null
        }
        else -> null
      }

      if(action != null) {
        event.consume()

        scope.launch {
          action()
        }
      }
    }
  }

  val awtMouseListener = AWTEventListener { event ->
    if(event is MouseEvent && event.id == MouseEvent.MOUSE_RELEASED && !event.isConsumed) {
      val action = when(event.button) {
        os.mouseBackButton -> backPressDispatcher::onBackPressed
        os.mouseForwardButton -> onForwardPress
        else -> null
      }

      if(action != null) {
        event.consume()

        scope.launch {
          action()
        }
      }
    }
  }

  DisposableEffect(Unit) {
    toolkit.addAWTEventListener(awtKeyListener, AWTEvent.KEY_EVENT_MASK)
    toolkit.addAWTEventListener(awtMouseListener, AWTEvent.MOUSE_EVENT_MASK)

    onDispose {
      toolkit.removeAWTEventListener(awtKeyListener)
      toolkit.removeAWTEventListener(awtMouseListener)
    }
  }
}

private val Os.mouseBackButton get() = if(this == Os.Linux) 6 else 4
private val Os.mouseForwardButton get() = if(this == Os.Linux) 7 else 5
