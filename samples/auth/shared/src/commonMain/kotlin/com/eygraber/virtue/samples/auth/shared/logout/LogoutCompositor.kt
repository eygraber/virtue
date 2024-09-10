package com.eygraber.virtue.samples.auth.shared.logout

import androidx.compose.runtime.Composable
import com.eygraber.vice.ViceCompositor
import com.eygraber.virtue.di.scopes.DestinationSingleton
import me.tatarka.inject.annotations.Inject

@DestinationSingleton
@Inject
class LogoutCompositor(
  private val stringsSource: LogoutStringsSource,
) : ViceCompositor<LogoutIntent, LogoutViewState> {
  @Composable
  override fun composite() = LogoutViewState(
    strings = stringsSource.currentState(),
  )

  override suspend fun onIntent(intent: LogoutIntent) {}
}
