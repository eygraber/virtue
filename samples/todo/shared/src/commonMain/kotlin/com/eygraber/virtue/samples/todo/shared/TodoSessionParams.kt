package com.eygraber.virtue.samples.todo.shared

import com.eygraber.virtue.session.VirtueSession

val TodoSessionParams = VirtueSession.Params(
  startDestination = Routes.Home,
  navGraphBuilder = TodoNavGraphBuilder,
  darkColorScheme = TodoTheme.darkColorScheme,
  lightColorScheme = TodoTheme.lightColorScheme,
)
