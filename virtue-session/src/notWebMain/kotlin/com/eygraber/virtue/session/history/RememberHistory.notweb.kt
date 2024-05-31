package com.eygraber.virtue.session.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
internal actual fun rememberHistory(): History = rememberSaveable(
  saver = TimelineHistory.Saver(),
) {
  TimelineHistory()
}
