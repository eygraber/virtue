package com.eygraber.virtue.session

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.eygraber.virtue.nav.DisplayableRoute

public interface VirtueNavGraphBuilder<T : GenericVirtueSessionComponent> {
  public fun NavGraphBuilder.buildGraph(
    displayRoute: (DisplayableRoute) -> Unit,
    sessionComponent: T,
    navController: NavHostController,
  )
}
