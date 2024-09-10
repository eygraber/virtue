package com.eygraber.virtue.samples.auth.shared.logout

import com.eygraber.vice.ViceEffects
import com.eygraber.virtue.auth.VirtueAuth
import com.eygraber.virtue.di.scopes.DestinationSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

@DestinationSingleton
@Inject
class LogoutEffects(
  private val auth: VirtueAuth,
  private val navigator: LogoutNavigator,
) : ViceEffects {
  override fun CoroutineScope.runEffects() {
    launch {
      if(auth.currentState() is VirtueAuth.State.LoggingOut) {
        delay(2.seconds)
        if(Random.nextBoolean()) {
          auth.onLogoutSucceeded()
        }
        else {
          auth.onError()
        }
      }

      withContext(Dispatchers.Main) {
        navigator.onNavigateToRoot()
      }
    }
  }
}
