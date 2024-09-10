package com.eygraber.virtue.samples.auth.shared.root

import com.eygraber.vice.ViceEffects
import com.eygraber.virtue.auth.VirtueAuth
import com.eygraber.virtue.di.scopes.DestinationSingleton
import com.eygraber.virtue.samples.auth.shared.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@DestinationSingleton
@Inject
class RootEffects(
  private val auth: VirtueAuth,
  private val navigator: RootNavigator,
  private val route: Routes.Root,
) : ViceEffects {
  override fun CoroutineScope.runEffects() {
    launch {
      val state = auth.stateFlow.first()
      withContext(Dispatchers.Main) {
        when(state) {
          is VirtueAuth.State.LoggingOut -> navigator.onNavigateToLogout()
          VirtueAuth.State.LoggedIn -> navigator.onNavigateToLoggedIn(route.redirect)
          VirtueAuth.State.LoggedOut -> navigator.onNavigateToLogin()
          VirtueAuth.State.RefreshRequired -> navigator.onNavigateToLogout()
          VirtueAuth.State.Error -> { /* handled by compositor */ }
        }
      }
    }
  }
}
