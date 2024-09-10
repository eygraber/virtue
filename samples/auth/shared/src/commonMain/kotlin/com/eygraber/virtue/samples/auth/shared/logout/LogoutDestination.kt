package com.eygraber.virtue.samples.auth.shared.logout

import com.eygraber.virtue.di.scopes.DestinationSingleton
import com.eygraber.virtue.samples.auth.shared.AuthDestinationComponentWithEffects
import com.eygraber.virtue.samples.auth.shared.AuthDestinationWithEffects
import com.eygraber.virtue.samples.auth.shared.AuthSessionComponent
import com.eygraber.virtue.samples.auth.shared.Routes
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.KmpComponentCreate
import me.tatarka.inject.annotations.Provides
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@DestinationSingleton
class LogoutNavigator(
  val onNavigateToRoot: () -> Unit,
)

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
class LogoutDestination(
  navigator: LogoutNavigator,
  override val parentComponent: AuthSessionComponent,
) : AuthDestinationWithEffects<Routes.Logout, LogoutIntent, LogoutCompositor, LogoutEffects, LogoutViewState>() {
  override val view: LogoutView = { state, onIntent -> LogoutView(state, onIntent) }

  override val component = LogoutComponent.createKmp(
    parentComponent = parentComponent,
    route = Routes.Logout,
    navigator = navigator,
  )
}

@DestinationSingleton
@Component
abstract class LogoutComponent(
  @Component override val parentComponent: AuthSessionComponent,
  override val route: Routes.Logout,
  @get:Provides val navigator: LogoutNavigator,
) : AuthDestinationComponentWithEffects<Routes.Logout, LogoutIntent, LogoutCompositor, LogoutEffects, LogoutViewState> {
  companion object
}

@KmpComponentCreate
internal expect fun LogoutComponent.Companion.createKmp(
  parentComponent: AuthSessionComponent,
  route: Routes.Logout,
  navigator: LogoutNavigator,
): LogoutComponent
