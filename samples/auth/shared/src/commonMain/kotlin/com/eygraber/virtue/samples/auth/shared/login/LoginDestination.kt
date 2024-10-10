package com.eygraber.virtue.samples.auth.shared.login

import com.eygraber.virtue.di.scopes.DestinationSingleton
import com.eygraber.virtue.samples.auth.shared.AuthDestination
import com.eygraber.virtue.samples.auth.shared.AuthDestinationComponent
import com.eygraber.virtue.samples.auth.shared.AuthSessionComponent
import com.eygraber.virtue.samples.auth.shared.Routes
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.KmpComponentCreate
import me.tatarka.inject.annotations.Provides

@DestinationSingleton
class LoginNavigator(
  val onNavigateToLoggedIn: () -> Unit,
)

class LoginDestination(
  navigator: LoginNavigator,
  override val parentComponent: AuthSessionComponent,
) : AuthDestination<Routes.Login, LoginIntent, LoginCompositor, LoginViewState>() {
  override val view: LoginView = { state, onIntent -> LoginView(state, onIntent) }

  override val component = LoginComponent.createKmp(
    parentComponent = parentComponent,
    route = Routes.Login,
    navigator = navigator,
  )
}

@DestinationSingleton
@Component
abstract class LoginComponent(
  @Component override val parentComponent: AuthSessionComponent,
  override val route: Routes.Login,
  @get:Provides val navigator: LoginNavigator,
) : AuthDestinationComponent<Routes.Login, LoginIntent, LoginCompositor, LoginViewState> {
  companion object
}

@KmpComponentCreate
internal expect fun LoginComponent.Companion.createKmp(
  parentComponent: AuthSessionComponent,
  route: Routes.Login,
  navigator: LoginNavigator,
): LoginComponent
