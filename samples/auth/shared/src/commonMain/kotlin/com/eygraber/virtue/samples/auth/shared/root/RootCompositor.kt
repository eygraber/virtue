package com.eygraber.virtue.samples.auth.shared.root

import androidx.compose.runtime.Composable
import com.eygraber.vice.ViceCompositor
import com.eygraber.vice.sources.FlowSource
import com.eygraber.virtue.app.killVirtueApp
import com.eygraber.virtue.auth.VirtueAuth
import com.eygraber.virtue.di.scopes.DestinationSingleton
import com.eygraber.virtue.storage.kv.VirtueKeyValueStorageCleaner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@DestinationSingleton
@Inject
class ShowErrorDialogSource(
  auth: VirtueAuth,
) : FlowSource<Boolean>() {
  override val flow: Flow<Boolean> =
    auth.stateFlow.map { it is VirtueAuth.State.Error }

  override val initial: Boolean = false
}

@DestinationSingleton
@Inject
class RootCompositor(
  private val auth: VirtueAuth,
  private val keyValueStorageCleaner: VirtueKeyValueStorageCleaner,
  private val showErrorDialogSource: ShowErrorDialogSource,
  private val stringsSource: RootStringsSource,
) : ViceCompositor<RootIntent, RootViewState> {
  @Composable
  override fun composite() = RootViewState(
    isErrorDialogShowing = showErrorDialogSource.currentState(),
    strings = stringsSource.currentState(),
  )

  override suspend fun onIntent(intent: RootIntent) {
    when(intent) {
      RootIntent.ClearData -> {
        keyValueStorageCleaner.cleanStorage()
        killVirtueApp()
      }

      RootIntent.Restart -> killVirtueApp()
    }
  }
}
