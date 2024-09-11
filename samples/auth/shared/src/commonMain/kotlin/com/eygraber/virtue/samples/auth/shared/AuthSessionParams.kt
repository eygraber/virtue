package com.eygraber.virtue.samples.auth.shared

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import com.eygraber.virtue.session.VirtueNavHostParams
import com.eygraber.virtue.session.VirtueSession.Params

val AuthSessionParams = Params(
  initialRoute = Routes.Root(),
  routeClass = Routes::class,
  navGraphBuilder = AuthNavGraphBuilder,
  darkColorScheme = AuthTheme.darkColorScheme,
  lightColorScheme = AuthTheme.lightColorScheme,
  navHostParams = VirtueNavHostParams(
    enterTransition = { slideInHorizontally(tween(500)) { it * 2 } },
    popEnterTransition = { slideInHorizontally(tween(500)) { -it } },
    popExitTransition = { slideOutHorizontally(tween(500)) { it * 2 } },
    exitTransition = { slideOutHorizontally(tween(500)) { -it } },
  ),
)
