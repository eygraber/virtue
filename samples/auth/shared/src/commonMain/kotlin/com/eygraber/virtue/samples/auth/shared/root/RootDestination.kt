package com.eygraber.virtue.samples.auth.shared.root

import com.eygraber.virtue.di.scopes.DestinationSingleton
import com.eygraber.virtue.samples.auth.shared.AuthDestinationComponentWithEffects
import com.eygraber.virtue.samples.auth.shared.AuthDestinationWithEffects
import com.eygraber.virtue.samples.auth.shared.AuthSessionComponent
import com.eygraber.virtue.samples.auth.shared.Routes
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.KmpComponentCreate
import me.tatarka.inject.annotations.Provides

@DestinationSingleton
class RootNavigator(
  val onNavigateToLogout: () -> Unit,
  val onNavigateToLogin: () -> Unit,
  val onNavigateToLoggedIn: (Routes) -> Unit,
)

class RootDestination(
  navigator: RootNavigator,
  route: Routes.Root,
  override val parentComponent: AuthSessionComponent,
) : AuthDestinationWithEffects<Routes.Root, RootIntent, RootCompositor, RootEffects, RootViewState>() {
  override val view: RootView = { state, onIntent -> RootView(state, onIntent) }

  override val component = RootComponent.createKmp(
    parentComponent = parentComponent,
    route = route,
    navigator = navigator,
  )
}

@DestinationSingleton
@Component
abstract class RootComponent(
  @Component override val parentComponent: AuthSessionComponent,
  @get:Provides override val route: Routes.Root,
  @get:Provides val navigator: RootNavigator,
) : AuthDestinationComponentWithEffects<Routes.Root, RootIntent, RootCompositor, RootEffects, RootViewState> {
  companion object
}

@KmpComponentCreate
internal expect fun RootComponent.Companion.createKmp(
  parentComponent: AuthSessionComponent,
  route: Routes.Root,
  navigator: RootNavigator,
): RootComponent
