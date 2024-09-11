package com.eygraber.virtue.samples.auth.shared

import com.eygraber.virtue.config.VirtueConfig
import com.eygraber.virtue.di.components.VirtueAppComponent
import com.eygraber.virtue.di.components.VirtuePlatformComponent
import com.eygraber.virtue.di.scopes.AppSingleton
import me.tatarka.inject.annotations.Component

@AppSingleton
@Component
abstract class AuthAppComponent(
  @Component override val platformComponent: VirtuePlatformComponent,
  override val config: VirtueConfig,
) : VirtueAppComponent {
  abstract override val initializer: AuthAppInitializer

  companion object
}
