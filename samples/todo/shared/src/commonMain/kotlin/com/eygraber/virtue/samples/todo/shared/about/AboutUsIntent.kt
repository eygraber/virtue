package com.eygraber.virtue.samples.todo.shared.about

sealed interface AboutUsIntent {
  data object BackPress : AboutUsIntent
  data object Close : AboutUsIntent
  data class BackHandlerEnableChange(val isEnabled: Boolean) : AboutUsIntent
}
