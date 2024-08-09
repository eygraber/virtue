package com.eygraber.virtue.samples.todo.shared.about

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.vice.ViceCompositor
import com.eygraber.virtue.di.scopes.DestinationSingleton
import me.tatarka.inject.annotations.Inject

@DestinationSingleton
@Inject
class AboutUsCompositor(
  private val navigator: AboutUsNavigator,
) : ViceCompositor<AboutUsIntent, AboutUsViewState> {
  private var isBackHandlerEnabled by mutableStateOf(false)
  private var backPressesHandled by mutableIntStateOf(0)

  @Composable
  override fun composite() = AboutUsViewState(
    isBackHandlerEnabled = isBackHandlerEnabled,
    backPressesHandled = backPressesHandled,
  )

  override suspend fun onIntent(intent: AboutUsIntent) {
    when(intent) {
      AboutUsIntent.BackPress -> backPressesHandled++
      AboutUsIntent.Close -> navigator.onNavigateBack()
      is AboutUsIntent.BackHandlerEnableChange -> {
        backPressesHandled = 0
        isBackHandlerEnabled = intent.isEnabled
      }
    }
  }
}
