package com.eygraber.virtue.nav

import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.eygraber.uri.Uri

public class VirtueDeepLink(
  public val uri: Uri,
  public val navOptions: NavOptions? = null,
  public val navigatorExtras: Navigator.Extras? = null,
)
