package com.eygraber.virtue.samples.auth.shared

import com.eygraber.virtue.config.VirtueConfig
import com.eygraber.virtue.di.components.VirtuePlatformComponent
import me.tatarka.inject.annotations.KmpComponentCreate

@KmpComponentCreate
expect fun AuthAppComponent.Companion.createKmp(
  platformComponent: VirtuePlatformComponent,
  config: VirtueConfig,
): AuthAppComponent
