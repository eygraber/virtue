@file:Suppress("MissingPackageDeclaration")

import com.eygraber.virtue.app.VirtueApplication
import com.eygraber.virtue.config.IosVirtueConfig
import com.eygraber.virtue.di.components.VirtuePlatformComponent
import com.eygraber.virtue.di.components.VirtuePlatformSessionComponent
import com.eygraber.virtue.samples.todo.shared.Routes
import com.eygraber.virtue.samples.todo.shared.TodoAppComponent
import com.eygraber.virtue.samples.todo.shared.TodoAppInfo
import com.eygraber.virtue.samples.todo.shared.TodoSessionComponent
import com.eygraber.virtue.samples.todo.shared.TodoSessionParams
import com.eygraber.virtue.samples.todo.shared.createKmp

class TodoIosApplication : VirtueApplication<TodoAppComponent, TodoSessionComponent, Routes>(
  config = IosVirtueConfig(
    appInfo = TodoAppInfo,
  ),
) {
  override fun createAppComponent(
    virtuePlatformComponent: VirtuePlatformComponent,
    config: IosVirtueConfig,
  ) = TodoAppComponent.createKmp(
    platformComponent = virtuePlatformComponent,
    config = config,
  )

  override fun createSessionComponent(
    appComponent: TodoAppComponent,
    virtuePlatformSessionComponent: VirtuePlatformSessionComponent,
  ) = TodoSessionComponent.createKmp(
    appComponent = appComponent,
    virtuePlatformSessionComponent = virtuePlatformSessionComponent,
  )

  override val sessionParams = TodoSessionParams

  override fun createViewController() = createVirtueViewController()
}
