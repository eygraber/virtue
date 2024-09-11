package com.eygraber.virtue.samples.auth.android

import com.eygraber.virtue.app.VirtueAndroidApplication
import com.eygraber.virtue.config.AndroidVirtueConfig
import com.eygraber.virtue.di.components.VirtuePlatformComponent
import com.eygraber.virtue.samples.auth.shared.AuthAppComponent
import com.eygraber.virtue.samples.auth.shared.AuthAppInfo
import com.eygraber.virtue.samples.auth.shared.create

class AuthAndroidApplication : VirtueAndroidApplication<AuthAppComponent>() {
  override val config = AndroidVirtueConfig(
    appInfo = AuthAppInfo,
  )

  override fun createAppComponent(
    virtuePlatformComponent: VirtuePlatformComponent,
  ): AuthAppComponent = AuthAppComponent.create(
    platformComponent = virtuePlatformComponent,
    config = config,
  )
}
