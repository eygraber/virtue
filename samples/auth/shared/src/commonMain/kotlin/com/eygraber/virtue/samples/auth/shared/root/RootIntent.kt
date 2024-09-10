package com.eygraber.virtue.samples.auth.shared.root

sealed interface RootIntent {
  data object Restart : RootIntent
  data object ClearData : RootIntent
}
