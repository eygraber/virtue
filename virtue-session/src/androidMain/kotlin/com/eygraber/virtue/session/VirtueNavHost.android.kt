package com.eygraber.virtue.session

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
public actual fun VirtueNavHost(
  navController: NavHostController,
  startDestination: Any,
  params: VirtueNavHostParams,
  builder: NavGraphBuilder.() -> Unit,
) {
  NavHost(
    navController = navController,
    startDestination = startDestination,
    modifier = params.modifier,
    contentAlignment = params.contentAlignment,
    enterTransition = params.enterTransition,
    exitTransition = params.exitTransition,
    popEnterTransition = params.popEnterTransition,
    popExitTransition = params.popExitTransition,
    sizeTransform = params.sizeTransform,
    builder = builder,
  )
}
