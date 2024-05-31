package com.eygraber.virtue.samples.todo.shared.settings

import androidx.compose.runtime.Composable
import com.eygraber.vice.ViceCompositor
import com.eygraber.virtue.di.scopes.DestinationSingleton
import me.tatarka.inject.annotations.Inject

@DestinationSingleton
@Inject
class SettingsCompositor(
  private val navigator: SettingsNavigator,
) : ViceCompositor<SettingsIntent, SettingsViewState> {
  @Composable
  override fun composite() = SettingsViewState

  override suspend fun onIntent(intent: SettingsIntent) {
    when(intent) {
      SettingsIntent.Close -> navigator.onNavigateBack()
      SettingsIntent.NavigateToAboutUs -> navigator.onNavigateToAboutUs()
    }
  }
}
