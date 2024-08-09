package com.eygraber.virtue.session

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import com.eygraber.vice.nav.LocalAnimatedVisibilityScope

@Composable
public fun VirtueAnimatedContentScope(
  content: @Composable AnimatedContentScope.() -> Unit,
) {
  val animatedContentScope = LocalAnimatedVisibilityScope.current
  require(animatedContentScope is AnimatedContentScope) {
    "LocalAnimatedVisibilityScope's current value is not an AnimatedContentScope"
  }

  with(animatedContentScope) {
    content()
  }
}
