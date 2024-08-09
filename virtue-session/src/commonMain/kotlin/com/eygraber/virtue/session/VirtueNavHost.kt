package com.eygraber.virtue.session

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.eygraber.virtue.session.nav.NestedVirtueRoute

@Suppress("ModifierMissing")
@Composable
public fun VirtueNavHost(
  navController: NavHostController,
  startDestination: Any,
  params: VirtueNavHostParams,
  builder: NavGraphBuilder.() -> Unit,
) {
  val actualStartDestination = remember(startDestination) {
    var sd = startDestination
    while(sd is NestedVirtueRoute) {
      sd = sd.parent()
    }
    sd
  }

  NavHost(
    navController = navController,
    startDestination = actualStartDestination,
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

public data class VirtueNavHostParams(
  val modifier: Modifier = Modifier,
  val contentAlignment: Alignment = Alignment.Center,
  val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
    { fadeIn(animationSpec = tween(700)) },
  val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
    { fadeOut(animationSpec = tween(700)) },
  val popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
    enterTransition,
  val popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
    exitTransition,
  val sizeTransform: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> SizeTransform?)? =
    null,
)
