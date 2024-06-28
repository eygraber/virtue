package com.eygraber.virtue.samples.todo.shared

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.eygraber.vice.nav.viceComposable
import com.eygraber.vice.nav.viceDialog
import com.eygraber.virtue.nav.VirtueRoute
import com.eygraber.virtue.nav.nestedGraph
import com.eygraber.virtue.samples.todo.shared.about.AboutUsDestination
import com.eygraber.virtue.samples.todo.shared.details.DetailsDestination
import com.eygraber.virtue.samples.todo.shared.home.HomeDestination
import com.eygraber.virtue.samples.todo.shared.settings.SettingsDestination
import com.eygraber.virtue.session.VirtueNavGraphBuilder

object TodoNavGraphBuilder : VirtueNavGraphBuilder<TodoSessionComponent> {
  override fun NavGraphBuilder.buildGraph(
    displayRoute: (VirtueRoute) -> Unit,
    sessionComponent: TodoSessionComponent,
    navController: NavHostController,
  ) {
    viceComposable<Routes.Home> { entry ->
      println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!${entry.route.display}")
      displayRoute(entry.route)

      HomeDestination(
        onNavigateToCreateItem = { navController.navigate(Routes.Details.Create) },
        onNavigateToUpdateItem = { id -> navController.navigate(Routes.Details.Update(id)) },
        onNavigateToSettings = { navController.navigate(Routes.Settings.Home) },
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
        println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!${entry.route.display}")
        displayRoute(entry.route)

        SettingsDestination(
          onNavigateBack = { navController.popBackStack() },
          onNavigateToAboutUs = {
            navController.navigate(Routes.Settings.AboutUs)
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
