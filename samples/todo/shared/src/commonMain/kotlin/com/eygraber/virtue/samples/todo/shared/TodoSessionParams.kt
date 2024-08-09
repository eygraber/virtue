package com.eygraber.virtue.samples.todo.shared

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import com.eygraber.virtue.session.VirtueNavHostParams
import com.eygraber.virtue.session.VirtueSession.Params

val TodoSessionParams = Params(
  initialRoute = Routes.Home,
  routeClass = Routes::class,
  navGraphBuilder = TodoNavGraphBuilder,
  darkColorScheme = TodoTheme.darkColorScheme,
  lightColorScheme = TodoTheme.lightColorScheme,
  navHostParams = VirtueNavHostParams(
    enterTransition = { slideInHorizontally(tween(500)) { it * 2 } },
    popEnterTransition = { slideInHorizontally(tween(500)) { -it } },
    popExitTransition = { slideOutHorizontally(tween(500)) { it * 2 } },
    exitTransition = { slideOutHorizontally(tween(500)) { -it } },
  ),
)
