package com.eygraber.virtue.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.Navigator
import androidx.navigation.compose.rememberNavController

@Composable
public actual fun rememberVirtueNavController(
  vararg navigators: Navigator<out NavDestination>,
): NavHostController = rememberNavController(*navigators)
