package com.eygraber.virtue.samples.todo.shared.about

import androidx.compose.runtime.Composable
import com.eygraber.vice.ViceCompositor
import com.eygraber.virtue.di.scopes.DestinationSingleton
import me.tatarka.inject.annotations.Inject

@DestinationSingleton
@Inject
class AboutUsCompositor(
  private val navigator: AboutUsNavigator,
) : ViceCompositor<AboutUsIntent, AboutUsViewState> {
  @Composable
  override fun composite() = AboutUsViewState

  override suspend fun onIntent(intent: AboutUsIntent) {
    when(intent) {
      AboutUsIntent.Close -> navigator.onNavigateBack()
    }
  }
}
