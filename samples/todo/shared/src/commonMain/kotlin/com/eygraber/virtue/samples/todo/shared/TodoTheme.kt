package com.eygraber.virtue.samples.todo.shared

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object TodoTheme {
  val lightColorScheme: ColorScheme = lightColorScheme()
  val darkColorScheme: ColorScheme = darkColorScheme(background = Color.Black)
}

@Composable
fun TodoPreviewTheme(
  colorScheme: ColorScheme = TodoTheme.darkColorScheme,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = colorScheme,
    content = content,
  )
}
