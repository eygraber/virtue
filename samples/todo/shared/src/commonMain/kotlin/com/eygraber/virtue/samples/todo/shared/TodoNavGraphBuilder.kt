package com.eygraber.virtue.samples.todo.shared

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.eygraber.vice.nav.viceComposable
import com.eygraber.vice.nav.viceDialog
import com.eygraber.virtue.nav.DisplayableRoute
import com.eygraber.virtue.nav.nestedGraph
import com.eygraber.virtue.nav.virtueNavigate
import com.eygraber.virtue.nav.virtuePopUpTo
import com.eygraber.virtue.samples.todo.shared.about.AboutUsDestination
import com.eygraber.virtue.samples.todo.shared.details.DetailsDestination
import com.eygraber.virtue.samples.todo.shared.home.HomeDestination
import com.eygraber.virtue.samples.todo.shared.settings.SettingsDestination
import com.eygraber.virtue.session.VirtueNavGraphBuilder

object TodoNavGraphBuilder : VirtueNavGraphBuilder<TodoSessionComponent> {
  override fun NavGraphBuilder.buildGraph(
    displayRoute: (DisplayableRoute) -> Unit,
    sessionComponent: TodoSessionComponent,
    navController: NavHostController,
  ) {
    viceComposable<Routes.Home> { entry ->
      displayRoute(entry.route)

      HomeDestination(
        onNavigateToCreateItem = { navController.virtueNavigate(Routes.Details.Create) },
        onNavigateToUpdateItem = { id -> navController.virtueNavigate(Routes.Details.Update(id)) },
        onNavigateToSettings = { navController.virtueNavigate(Routes.Settings.Home) },
        parentComponent = sessionComponent,
      )
    }

    viceDialog<Routes.Details.Create> { entry ->
      displayRoute(entry.route)

      DetailsDestination(
        op = Routes.Details.Create,
        onNavigateBack = { navController.popBackStack() },
        parentComponent = sessionComponent,
      )
    }

    viceDialog<Routes.Details.Update> { entry ->
      displayRoute(entry.route)

      DetailsDestination(
        op = entry.route,
        onNavigateBack = { navController.popBackStack() },
        parentComponent = sessionComponent,
      )
    }

    nestedGraph<Routes.Settings>(
      startDestination = Routes.Settings.Home,
    ) {
      viceComposable<Routes.Settings.Home> { entry ->
        displayRoute(entry.route)

        SettingsDestination(
          onNavigateBack = { navController.popBackStack() },
          onNavigateToAboutUs = {
            navController.virtueNavigate(Routes.Settings.AboutUs) {
              virtuePopUpTo(Routes.Settings.Home) {
                inclusive = true
                saveState = true
              }
            }
          },
          parentComponent = sessionComponent,
        )
      }

      viceComposable<Routes.Settings.AboutUs> { entry ->
        displayRoute(entry.route)

        AboutUsDestination(
          onNavigateBack = { navController.popBackStack() },
          parentComponent = sessionComponent,
        )
      }
    }
  }
}
