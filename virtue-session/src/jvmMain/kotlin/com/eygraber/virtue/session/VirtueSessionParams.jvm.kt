package com.eygraber.virtue.session

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.WindowState

@Suppress("BooleanPropertyNaming")
public actual data class VirtueSessionParams(
  val windowState: WindowState = WindowState(),
  val minWindowSize: DpSize? = null,
  val visible: Boolean = true,
  val title: String = "Untitled",
  val icon: Painter? = null,
  val undecorated: Boolean = false,
  val transparent: Boolean = false,
  val resizable: Boolean = true,
  val enabled: Boolean = true,
  val focusable: Boolean = true,
  val alwaysOnTop: Boolean = false,
  val onPreviewKeyEvent: (KeyEvent) -> Boolean = { false },
  val onKeyEvent: (KeyEvent) -> Boolean = { false },
  val startDestination: Any? = null,
)
