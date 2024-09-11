package com.eygraber.virtue.samples.auth.shared.logged_in

import com.eygraber.vice.sources.LoadableFlowSource
import com.eygraber.virtue.di.scopes.DestinationSingleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@DestinationSingleton
@Inject
@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
class LoggedInStringsSource(
  dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : LoadableFlowSource<LoggedInStrings>() {
  override val placeholder = LoggedInStrings(
    title = "-".repeat(5),
    content = "-".repeat(10),
    logoutButton = "-".repeat(5),
  )

  override val dataFlow = flow {
    emit(
      // https://youtrack.jetbrains.com/issue/CMP-6631
      LoggedInStrings(
        title = "Logged In", // getString(Res.string.logged_in_title)
        content = "You are logged in!", // getString(Res.string.logged_in_content)
        logoutButton = "Logout", // getString(Res.string.logged_in_logout_button)
      ),
    )
  }.flowOn(dispatcher)
}
