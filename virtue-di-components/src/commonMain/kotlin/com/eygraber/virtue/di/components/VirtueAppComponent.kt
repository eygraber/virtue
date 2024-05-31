package com.eygraber.virtue.di.components

import com.eygraber.virtue.config.VirtueConfig
import com.eygraber.virtue.di.scopes.AppSingleton
import com.eygraber.virtue.theme.ThemeSettings
import me.tatarka.inject.annotations.Provides

@AppSingleton
public interface VirtueAppComponent {
  public val platformComponent: VirtuePlatformComponent

  @AppSingleton @get:Provides public val config: VirtueConfig

  public val themeSettings: ThemeSettings
}
