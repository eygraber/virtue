package com.eygraber.virtue.samples.todo.shared

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import com.eygraber.virtue.session.VirtueNavHostParams
import com.eygraber.virtue.session.VirtueSession.Params

val TodoSessionParams = Params(
  initialRoute = Routes.Home,
  routeClass = Routes::class,
  navGraphBuilder = TodoNavGraphBuilder,
  theme = { isApplicationInDarkMode, content ->
    MaterialTheme(
      colorScheme = when {
        isApplicationInDarkMode -> TodoTheme.darkColorScheme
        else -> TodoTheme.lightColorScheme
      },
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(MaterialTheme.colorScheme.background),
      ) {
        content()
      }
    }
  },
  navHostParams = VirtueNavHostParams(
    enterTransition = { slideInHorizontally(tween(500)) { it * 2 } },
    popEnterTransition = { slideInHorizontally(tween(500)) { -it } },
    popExitTransition = { slideOutHorizontally(tween(500)) { it * 2 } },
    exitTransition = { slideOutHorizontally(tween(500)) { -it } },
  ),
)
