package com.eygraber.virtue.samples.auth.shared

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

val AuthSessionParams = Params(
  initialRoute = Routes.Root(),
  routeClass = Routes::class,
  navGraphBuilder = AuthNavGraphBuilder,
  theme = { isApplicationInDarkMode, content ->
    MaterialTheme(
      colorScheme = when {
        isApplicationInDarkMode -> AuthTheme.darkColorScheme
        else -> AuthTheme.lightColorScheme
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
