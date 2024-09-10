package com.eygraber.virtue.samples.auth.shared

import androidx.compose.animation.AnimatedContent
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.eygraber.vice.nav.LocalAnimatedVisibilityScope

object AuthTheme {
  val lightColorScheme: ColorScheme = lightColorScheme()
  val darkColorScheme: ColorScheme = darkColorScheme(background = Color.Black)
}

@Composable
fun AuthPreviewTheme(
  colorScheme: ColorScheme = AuthTheme.darkColorScheme,
  content: @Composable () -> Unit,
) {
  AnimatedContent(targetState = 0) {
    CompositionLocalProvider(
      LocalAnimatedVisibilityScope provides this,
    ) {
      MaterialTheme(
        colorScheme = colorScheme,
        content = content,
      )
    }
  }
}
