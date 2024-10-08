package com.eygraber.virtue.samples.todo.desktop

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.eygraber.virtue.app.virtueApplication
import com.eygraber.virtue.config.DesktopVirtueConfig
import com.eygraber.virtue.samples.todo.shared.TodoAppComponent
import com.eygraber.virtue.samples.todo.shared.TodoAppInfo
import com.eygraber.virtue.samples.todo.shared.TodoSessionComponent
import com.eygraber.virtue.samples.todo.shared.TodoSessionParams
import com.eygraber.virtue.samples.todo.shared.create

fun main() = virtueApplication(
  appComponentFactory = { virtuePlatformComponent, config ->
    TodoAppComponent.create(
      platformComponent = virtuePlatformComponent,
      config = config,
    )
  },
  initialSessionComponentFactory = { appComponent, virtuePlatformSessionComponent ->
    TodoSessionComponent.create(
      appComponent = appComponent,
      virtuePlatformSessionComponent = virtuePlatformSessionComponent,
    )
  },
  config = DesktopVirtueConfig(
    appInfo = TodoAppInfo,

  ),
  sessionParams = TodoSessionParams,
  configureInitialSessionParams = { params, _ ->
    params.copy(
      minWindowSize = DpSize(400.dp, 400.dp),
    )
  },
)
