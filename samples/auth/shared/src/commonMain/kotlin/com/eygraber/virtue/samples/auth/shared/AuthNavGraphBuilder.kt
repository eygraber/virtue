package com.eygraber.virtue.samples.auth.shared

import androidx.navigation.NavGraphBuilder
import com.eygraber.vice.nav.viceComposable
import com.eygraber.virtue.samples.auth.shared.logged_in.LoggedInDestination
import com.eygraber.virtue.samples.auth.shared.logged_in.LoggedInNavigator
import com.eygraber.virtue.samples.auth.shared.login.LoginDestination
import com.eygraber.virtue.samples.auth.shared.login.LoginNavigator
import com.eygraber.virtue.samples.auth.shared.logout.LogoutDestination
import com.eygraber.virtue.samples.auth.shared.logout.LogoutNavigator
import com.eygraber.virtue.samples.auth.shared.root.RootDestination
import com.eygraber.virtue.samples.auth.shared.root.RootNavigator
import com.eygraber.virtue.session.VirtueNavGraphBuilder
import com.eygraber.virtue.session.nav.VirtueNavController

object AuthNavGraphBuilder : VirtueNavGraphBuilder<AuthSessionComponent, Routes> {
  override fun NavGraphBuilder.buildGraph(
    sessionComponent: AuthSessionComponent,
    initialRoute: Routes,
    navController: VirtueNavController<Routes>,
  ) {
    viceComposable<Routes.Root> { entry ->
      RootDestination(
        navigator = RootNavigator(
          onNavigateToLogout = { navController.navigate(Routes.Logout) },
          onNavigateToLogin = { navController.navigate(Routes.Login) },
          onNavigateToLoggedIn = { navController.navigate(Routes.LoggedIn) },
        ),
        route = entry.route,
        parentComponent = sessionComponent,
      )
    }

    viceComposable<Routes.Login> {
      LoginDestination(
        navigator = LoginNavigator(
          onNavigateToLoggedIn = { navController.navigate(Routes.LoggedIn) },
        ),
        parentComponent = sessionComponent,
      )
    }

    viceComposable<Routes.LoggedIn> {
      LoggedInDestination(
        parentComponent = sessionComponent,
        navigator = LoggedInNavigator(
          onNavigateToRoot = { navController.navigate(Routes.Root()) },
        ),
      )
    }

    viceComposable<Routes.Logout> {
      LogoutDestination(
        navigator = LogoutNavigator(
          onNavigateToRoot = { navController.navigate(Routes.Root()) },
        ),
        parentComponent = sessionComponent,
      )
    }
  }
}
