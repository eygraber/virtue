package com.eygraber.virtue.back.press.dispatch

import androidx.compose.runtime.Composable

@Suppress("ModifierMissing")
@Composable
public expect fun PlatformNavigationHandler(
  onForwardPress: () -> Unit,
)
