package com.eygraber.virtue.nav

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.Navigator

public actual fun <T : Any> NavController.virtueNavigate(
  route: T,
  builder: NavOptionsBuilder.() -> Unit,
) {
  @Suppress("NotImplementedDeclaration")
  TODO("Not implemented yet; waiting for CMP navigation to support type safe APIs")
}

public actual fun <T : Any> NavController.virtueNavigate(
  route: T,
  navOptions: NavOptions?,
  navigatorExtras: Navigator.Extras?,
) {
  @Suppress("NotImplementedDeclaration")
  TODO("Not implemented yet; waiting for CMP navigation to support type safe APIs")
}
