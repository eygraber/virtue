package com.eygraber.virtue.theme

public expect class ThemeSettingStorage {
  public suspend fun load(): ThemeSetting?
  public suspend fun store(setting: ThemeSetting)
}
