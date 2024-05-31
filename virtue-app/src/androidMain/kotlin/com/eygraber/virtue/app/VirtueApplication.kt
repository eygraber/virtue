package com.eygraber.virtue.app

import com.eygraber.virtue.config.VirtueConfig
import com.eygraber.virtue.di.components.VirtueAppComponent
import com.eygraber.virtue.di.components.VirtuePlatformComponent
import com.eygraber.virtue.theme.ThemeSetting

public interface VirtueApplication<A : VirtueAppComponent> {
  public val virtuePlatformComponent: VirtuePlatformComponent
  public val appComponent: A

  public val config: VirtueConfig

  public val defaultThemeSetting: ThemeSetting get() = ThemeSetting.System

  public suspend fun initialize() {
    appComponent.themeSettings.initialize(
      default = defaultThemeSetting,
    )
  }
}
