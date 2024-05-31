package com.eygraber.virtue.samples.todo.shared.settings

sealed interface SettingsIntent {
  data object Close : SettingsIntent
  data object NavigateToAboutUs : SettingsIntent
}
