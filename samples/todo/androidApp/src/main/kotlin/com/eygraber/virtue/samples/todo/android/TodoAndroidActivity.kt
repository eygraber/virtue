package com.eygraber.virtue.samples.todo.android

import com.eygraber.virtue.app.VirtueAndroidActivity
import com.eygraber.virtue.di.components.VirtuePlatformSessionComponent
import com.eygraber.virtue.samples.todo.shared.TodoAppComponent
import com.eygraber.virtue.samples.todo.shared.TodoSessionComponent
import com.eygraber.virtue.samples.todo.shared.TodoSessionParams
import com.eygraber.virtue.samples.todo.shared.create

class TodoAndroidActivity : VirtueAndroidActivity<TodoAppComponent, TodoSessionComponent>() {
  override val sessionParams = TodoSessionParams

  override fun createSessionComponent(
    appComponent: TodoAppComponent,
    virtuePlatformSessionComponent: VirtuePlatformSessionComponent,
  ) = TodoSessionComponent.create(
    appComponent = appComponent,
    virtuePlatformSessionComponent = virtuePlatformSessionComponent,
  )
}
