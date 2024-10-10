package com.eygraber.virtue.samples.auth.shared.login

import com.eygraber.vice.sources.LoadableFlowSource
import com.eygraber.virtue.di.scopes.DestinationSingleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject

@DestinationSingleton
@Inject
class LoginStringsSource(
  dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : LoadableFlowSource<LoginStrings>() {
  override val placeholder = LoginStrings(
    title = "-".repeat(5),
    tokenLabel = "-".repeat(10),
    button = "-".repeat(5),
  )

  override val dataFlow = flow {
    emit(
      // https://youtrack.jetbrains.com/issue/CMP-6631
      LoginStrings(
        title = "Login", // getString(Res.string.login_title)
        tokenLabel = "Enter your token:", // getString(Res.string.login_token_label)
        button = "Login", // getString(Res.string.login_button)
      ),
    )
  }.flowOn(dispatcher)
}
