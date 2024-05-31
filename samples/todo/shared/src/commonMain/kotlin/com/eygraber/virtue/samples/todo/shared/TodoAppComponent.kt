package com.eygraber.virtue.samples.todo.shared

import com.eygraber.virtue.config.VirtueConfig
import com.eygraber.virtue.di.components.VirtueAppComponent
import com.eygraber.virtue.di.components.VirtuePlatformComponent
import me.tatarka.inject.annotations.Component

@Component
abstract class TodoAppComponent(
  @Component override val platformComponent: VirtuePlatformComponent,
  override val config: VirtueConfig,
) : VirtueAppComponent {
  companion object
}
