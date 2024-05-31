package com.eygraber.virtue.nav

import androidx.navigation.NavOptionsBuilder
import androidx.navigation.PopUpToBuilder

public expect fun <T : Any> NavOptionsBuilder.virtuePopUpTo(route: T, popUpToBuilder: PopUpToBuilder.() -> Unit)
