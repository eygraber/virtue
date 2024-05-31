package com.eygraber.virtue.back.press.dispatch

import androidx.compose.runtime.Composable

@Composable
public actual fun BackHandler(enabled: Boolean, onBackPress: () -> Unit) {
  androidx.activity.compose.BackHandler(enabled, onBackPress)
}
