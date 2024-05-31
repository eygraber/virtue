package com.eygraber.virtue.theme.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.eygraber.virtue.theme.ThemeSetting
import com.eygraber.virtue.theme.ThemeSettings

@Composable
public fun ThemeSettings.isApplicationInDarkTheme(): Boolean {
  val setting by setting.collectAsState()
  return when(setting) {
    ThemeSetting.Dark -> true
    ThemeSetting.Light -> false
    ThemeSetting.System -> isSystemInDarkTheme()
  }
}
