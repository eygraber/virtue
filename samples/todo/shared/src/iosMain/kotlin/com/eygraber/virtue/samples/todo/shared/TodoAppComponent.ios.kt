package com.eygraber.virtue.samples.todo.shared

import com.eygraber.virtue.config.VirtueConfig
import com.eygraber.virtue.di.components.VirtuePlatformComponent
import me.tatarka.inject.annotations.KmpComponentCreate

@KmpComponentCreate
expect fun TodoAppComponent.Companion.createKmp(
  platformComponent: VirtuePlatformComponent,
  config: VirtueConfig,
): TodoAppComponent
