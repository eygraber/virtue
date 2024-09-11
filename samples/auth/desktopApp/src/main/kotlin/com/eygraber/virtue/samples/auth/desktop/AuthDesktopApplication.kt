package com.eygraber.virtue.samples.auth.desktop

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.eygraber.virtue.app.virtueApplication
import com.eygraber.virtue.config.DesktopVirtueConfig
import com.eygraber.virtue.samples.auth.shared.AuthAppComponent
import com.eygraber.virtue.samples.auth.shared.AuthAppInfo
import com.eygraber.virtue.samples.auth.shared.AuthSessionComponent
import com.eygraber.virtue.samples.auth.shared.AuthSessionParams
import com.eygraber.virtue.samples.auth.shared.create

fun main() = virtueApplication(
  appComponentFactory = { virtuePlatformComponent, config ->
    AuthAppComponent.create(
      platformComponent = virtuePlatformComponent,
      config = config,
    )
  },
  initialSessionComponentFactory = { appComponent, virtuePlatformSessionComponent ->
    AuthSessionComponent.create(
      appComponent = appComponent,
      virtuePlatformSessionComponent = virtuePlatformSessionComponent,
    )
  },
  config = DesktopVirtueConfig(
    appInfo = AuthAppInfo,

  ),
  sessionParams = AuthSessionParams,
  configureInitialSessionParams = { params, _ ->
    params.copy(
      minWindowSize = DpSize(400.dp, 400.dp),
    )
  },
)
