package com.eygraber.virtue.nav

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.Navigator

public actual fun <T : Any> NavController.virtueNavigate(
  route: T,
  builder: NavOptionsBuilder.() -> Unit,
) {
  navigate(
    route = route,
    builder = builder,
  )
}

public actual fun <T : Any> NavController.virtueNavigate(
  route: T,
  navOptions: NavOptions?,
  navigatorExtras: Navigator.Extras?,
) {
  navigate(
    route = route,
    navOptions,
    navigatorExtras,
  )
}
