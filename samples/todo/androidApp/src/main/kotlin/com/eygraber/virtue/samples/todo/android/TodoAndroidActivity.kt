package com.eygraber.virtue.samples.todo.android

import com.eygraber.uri.Uri
import com.eygraber.virtue.app.VirtueActivity
import com.eygraber.virtue.di.components.VirtuePlatformSessionComponent
import com.eygraber.virtue.samples.todo.shared.Routes
import com.eygraber.virtue.samples.todo.shared.TodoAppComponent
import com.eygraber.virtue.samples.todo.shared.TodoSessionComponent
import com.eygraber.virtue.samples.todo.shared.TodoSessionParams
import com.eygraber.virtue.samples.todo.shared.create
import com.eygraber.virtue.session.nav.VirtueDeepLink

class TodoAndroidActivity : VirtueActivity<TodoAppComponent, TodoSessionComponent, Routes>() {
  override val sessionParams = TodoSessionParams

  override fun createDeepLink(deepLink: Uri) = Routes.fromUri(deepLink)?.let { route ->
    VirtueDeepLink(route)
  }

  override fun createSessionComponent(
    appComponent: TodoAppComponent,
    virtuePlatformSessionComponent: VirtuePlatformSessionComponent,
  ) = TodoSessionComponent.create(
    appComponent = appComponent,
    virtuePlatformSessionComponent = virtuePlatformSessionComponent,
  )
}
