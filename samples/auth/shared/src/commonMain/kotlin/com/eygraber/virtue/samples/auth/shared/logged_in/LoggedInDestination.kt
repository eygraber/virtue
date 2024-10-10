package com.eygraber.virtue.samples.auth.shared.logged_in

import com.eygraber.virtue.di.scopes.DestinationSingleton
import com.eygraber.virtue.samples.auth.shared.AuthDestination
import com.eygraber.virtue.samples.auth.shared.AuthDestinationComponent
import com.eygraber.virtue.samples.auth.shared.AuthSessionComponent
import com.eygraber.virtue.samples.auth.shared.Routes
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.KmpComponentCreate
import me.tatarka.inject.annotations.Provides

@DestinationSingleton
class LoggedInNavigator(
  val onNavigateToRoot: () -> Unit,
)

class LoggedInDestination(
  override val parentComponent: AuthSessionComponent,
  private val navigator: LoggedInNavigator,
) : AuthDestination<Routes.LoggedIn, LoggedInIntent, LoggedInCompositor, LoggedInViewState>() {
  override val view: LoggedInView = { state, onIntent -> LoggedInView(state, onIntent) }

  override val component = LoggedInComponent.createKmp(
    parentComponent = parentComponent,
    route = Routes.LoggedIn,
    navigator = navigator,
  )
}

@DestinationSingleton
@Component
abstract class LoggedInComponent(
  @Component override val parentComponent: AuthSessionComponent,
  override val route: Routes.LoggedIn,
  @get:Provides val navigator: LoggedInNavigator,
) : AuthDestinationComponent<Routes.LoggedIn, LoggedInIntent, LoggedInCompositor, LoggedInViewState> {
  companion object
}

@KmpComponentCreate
internal expect fun LoggedInComponent.Companion.createKmp(
  parentComponent: AuthSessionComponent,
  route: Routes.LoggedIn,
  navigator: LoggedInNavigator,
): LoggedInComponent
