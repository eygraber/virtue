package com.eygraber.virtue.samples.todo.android

import com.eygraber.virtue.app.VirtueAndroidApplication
import com.eygraber.virtue.config.AndroidVirtueConfig
import com.eygraber.virtue.di.components.VirtuePlatformComponent
import com.eygraber.virtue.samples.todo.shared.APP_NAME
import com.eygraber.virtue.samples.todo.shared.TodoAppComponent
import com.eygraber.virtue.samples.todo.shared.create

class TodoAndroidApplication : VirtueAndroidApplication<TodoAppComponent>() {
  override val config = AndroidVirtueConfig(
    appName = APP_NAME,
  )

  override fun createAppComponent(
    virtuePlatformComponent: VirtuePlatformComponent,
  ): TodoAppComponent = TodoAppComponent.create(
    platformComponent = virtuePlatformComponent,
    config = config,
  )
}
