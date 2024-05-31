package com.eygraber.virtue.session

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

@Composable
public actual fun VirtueNavHost(
  navController: NavHostController,
  startDestination: Any,
  params: VirtueNavHostParams,
  builder: NavGraphBuilder.() -> Unit,
) {
  @Suppress("NotImplementedDeclaration")
  TODO("Not implemented yet; waiting for CMP navigation to support type safe APIs")
}
