package com.eygraber.virtue.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.Navigator

/**
 * Creates a NavHostController that handles the adding of the [ComposeNavigator] and
 * [DialogNavigator]. Additional [Navigator] instances can be passed through [navigators] to
 * be applied to the returned NavController. Note that each [Navigator] must be separately
 * remembered before being passed in here: any changes to those inputs will cause the
 * NavController to be recreated.
 *
 * @see NavHost
 */
@Composable
public expect fun rememberVirtueNavController(
  vararg navigators: Navigator<out NavDestination>,
): NavHostController

public expect fun <T : Any> NavController.virtueNavigate(
  route: T,
  builder: NavOptionsBuilder.() -> Unit,
)

public expect fun <T : Any> NavController.virtueNavigate(
  route: T,
  navOptions: NavOptions? = null,
  navigatorExtras: Navigator.Extras? = null,
)
