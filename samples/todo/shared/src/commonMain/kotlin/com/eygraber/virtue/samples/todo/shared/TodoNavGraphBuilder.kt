package com.eygraber.virtue.samples.todo.shared

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.eygraber.vice.nav.viceComposable
import com.eygraber.vice.nav.viceDialog
import com.eygraber.virtue.samples.todo.shared.about.AboutUsDestination
import com.eygraber.virtue.samples.todo.shared.details.DetailsDestination
import com.eygraber.virtue.samples.todo.shared.home.HomeDestination
import com.eygraber.virtue.samples.todo.shared.settings.SettingsDestination
import com.eygraber.virtue.session.VirtueNavGraphBuilder
import com.eygraber.virtue.session.nav.VirtueNavController

object TodoNavGraphBuilder : VirtueNavGraphBuilder<TodoSessionComponent, Routes> {
  override fun NavGraphBuilder.buildGraph(
    sessionComponent: TodoSessionComponent,
    initialRoute: Routes,
    navController: VirtueNavController<Routes>,
  ) {
    viceComposable<Routes.Home> {
      HomeDestination(
        onNavigateToCreateItem = { navController.navigate(Routes.Details.Create) },
        onNavigateToUpdateItem = { id -> navController.navigate(Routes.Details.Update(id)) },
        onNavigateToSettings = { navController.navigate(Routes.Settings.Home) },
        parentComponent = sessionComponent,
      )
    }

    viceDialog<Routes.Details.Create> {
      DetailsDestination(
        op = Routes.Details.Create,
        onNavigateBack = { navController.popBackStack() },
        parentComponent = sessionComponent,
      )
    }

    viceDialog<Routes.Details.Update> { entry ->
      DetailsDestination(
        op = entry.route,
        onNavigateBack = { navController.popBackStack() },
        parentComponent = sessionComponent,
      )
    }

    navigation<Routes.Settings>(
      startDestination = if(initialRoute is Routes.Settings.Nested) initialRoute else Routes.Settings.Home,
    ) {
      viceComposable<Routes.Settings.Home> {
        SettingsDestination(
          onNavigateBack = { navController.navigateUp() },
          onNavigateToAboutUs = {
            navController.navigate(Routes.Settings.AboutUs)
          },
          parentComponent = sessionComponent,
        )
      }

      viceComposable<Routes.Settings.AboutUs> {
        AboutUsDestination(
          onNavigateBack = { navController.navigateUp() },
          parentComponent = sessionComponent,
        )
      }
    }
  }
}
