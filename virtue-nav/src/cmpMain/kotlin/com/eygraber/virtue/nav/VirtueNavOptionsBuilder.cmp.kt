package com.eygraber.virtue.nav

import androidx.navigation.NavOptionsBuilder
import androidx.navigation.PopUpToBuilder

public actual fun <T : Any> NavOptionsBuilder.virtuePopUpTo(route: T, popUpToBuilder: PopUpToBuilder.() -> Unit) {
  @Suppress("NotImplementedDeclaration")
  TODO("Not implemented yet; waiting for CMP navigation to support type safe APIs")
}
