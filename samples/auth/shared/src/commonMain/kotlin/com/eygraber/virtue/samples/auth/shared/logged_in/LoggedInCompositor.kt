package com.eygraber.virtue.samples.auth.shared.logged_in

import androidx.compose.runtime.Composable
import com.eygraber.vice.ViceCompositor
import com.eygraber.virtue.auth.VirtueAuth
import com.eygraber.virtue.di.scopes.DestinationSingleton
import me.tatarka.inject.annotations.Inject

@DestinationSingleton
@Inject
class LoggedInCompositor(
  private val auth: VirtueAuth,
  private val navigator: LoggedInNavigator,
  private val stringsSource: LoggedInStringsSource,
) : ViceCompositor<LoggedInIntent, LoggedInViewState> {
  @Composable
  override fun composite() = LoggedInViewState(
    strings = stringsSource.currentState(),
  )

  override suspend fun onIntent(intent: LoggedInIntent) {
    when(intent) {
      LoggedInIntent.Logout -> {
        auth.startLogout(VirtueAuth.State.LoggingOut.Manually)
        navigator.onNavigateToRoot()
      }
    }
  }
}
