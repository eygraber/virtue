package com.eygraber.virtue.session

import androidx.navigation.NavGraphBuilder
import com.eygraber.virtue.session.nav.VirtueNavController
import com.eygraber.virtue.session.nav.VirtueRoute

public interface VirtueNavGraphBuilder<T : VirtueSessionComponent, VR : VirtueRoute> {
  public fun NavGraphBuilder.buildGraph(
    sessionComponent: T,
    initialRoute: VR,
    navController: VirtueNavController<VR>,
  )
}
