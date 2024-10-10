package com.eygraber.virtue.samples.auth.shared.root

import com.eygraber.vice.sources.LoadableFlowSource
import com.eygraber.virtue.di.scopes.DestinationSingleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject

@DestinationSingleton
@Inject
class RootStringsSource(
  dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : LoadableFlowSource<RootStrings>() {
  override val placeholder = RootStrings(
    errorDialogTitle = "-".repeat(5),
    errorDialogText = "-".repeat(15),
    errorDialogRestart = "-".repeat(5),
    errorDialogClearData = "-".repeat(8),
  )

  @Suppress("ktlint:standard:max-line-length")
  override val dataFlow = flow {
    emit(
      // https://youtrack.jetbrains.com/issue/CMP-6631
      RootStrings(
        errorDialogTitle = "Auth Error", // getString(Res.string.auth_error_dialog_title)
        errorDialogText = "A fatal error occurred. You can try restarting the app, and if that doesn't work you can clear the app's data.", // getString(Res.string.auth_error_dialog_text)
        errorDialogRestart = "Restart", // getString(Res.string.auth_error_dialog_restart)
        errorDialogClearData = "Clear Data", // getString(Res.string.auth_error_dialog_clear_data)
      ),
    )
  }.flowOn(dispatcher)
}
