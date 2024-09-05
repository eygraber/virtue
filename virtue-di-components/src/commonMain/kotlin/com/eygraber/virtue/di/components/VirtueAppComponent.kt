package com.eygraber.virtue.di.components

import com.eygraber.virtue.config.VirtueAppInfo
import com.eygraber.virtue.config.VirtueConfig
import com.eygraber.virtue.di.scopes.AppSingleton
import com.eygraber.virtue.init.VirtueAppInitializer
import com.eygraber.virtue.paths.VirtuePathsProvider
import com.eygraber.virtue.theme.ThemeSettings
import me.tatarka.inject.annotations.Provides

@AppSingleton
public interface VirtueAppComponent : VirtuePathsProvider {
  public val platformComponent: VirtuePlatformComponent

  @AppSingleton @get:Provides public val config: VirtueConfig
  @AppSingleton @get:Provides public val appInfo: VirtueAppInfo get() = config.appInfo

  public val themeSettings: ThemeSettings

  /**
   * Components that implement VirtueAppComponent need to override this as an abstract
   * property with a concrete type (e.g. `abstract override val initializer: MyAppInitializer`) and
   * make sure that the concrete type participates in DI (either by annotating the concrete type with `@Inject`
   * or adding an `@Provides` for it).
   */
  public val initializer: VirtueAppInitializer
}
