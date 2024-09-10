package com.eygraber.virtue.samples.auth.shared.logout

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
class LogoutStringsSource(
  dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : LoadableFlowSource<LogoutStrings>() {
  override val placeholder = LogoutStrings(
    title = "-".repeat(5),
  )

  override val dataFlow = flow {
    emit(
      // https://youtrack.jetbrains.com/issue/CMP-6631
      LogoutStrings(
        title = "Logging you out…", // getString(Res.string.logout_title)
      ),
    )
  }.flowOn(dispatcher)
}
