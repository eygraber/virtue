package com.eygraber.virtue.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.eygraber.virtue.theme.ThemeSetting
import com.eygraber.virtue.theme.ThemeSettings

@Composable
internal fun InitializationEffect(
  themeSettings: ThemeSettings,
  defaultThemeSetting: ThemeSetting,
) {
  LaunchedEffect(Unit) {
    themeSettings.initialize(defaultThemeSetting)
  }
}
