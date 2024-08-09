package com.eygraber.virtue.session.nav

import androidx.navigation.NavOptions
import androidx.navigation.Navigator

public class VirtueDeepLink<VR : VirtueRoute>(
  public val route: VR,
  public val navOptions: NavOptions? = null,
  public val navigatorExtras: Navigator.Extras? = null,
)
