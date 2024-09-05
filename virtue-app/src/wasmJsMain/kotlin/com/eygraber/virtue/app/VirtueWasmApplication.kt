package com.eygraber.virtue.app

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.eygraber.uri.Uri
import com.eygraber.virtue.back.press.dispatch.OnBackPressDispatcherProvider
import com.eygraber.virtue.back.press.dispatch.WithBackPressDispatching
import com.eygraber.virtue.config.WasmVirtueConfig
import com.eygraber.virtue.di.components.VirtueAppComponent
import com.eygraber.virtue.di.components.VirtuePlatformComponent
import com.eygraber.virtue.di.components.VirtuePlatformSessionComponent
import com.eygraber.virtue.di.components.VirtueWebPlatformComponent
import com.eygraber.virtue.di.components.create
import com.eygraber.virtue.session.VirtueSession
import com.eygraber.virtue.session.VirtueSessionComponent
import com.eygraber.virtue.session.nav.VirtueRoute
import com.eygraber.virtue.theme.ThemeSetting

@OptIn(ExperimentalComposeUiApi::class)
public fun <A : VirtueAppComponent, S : VirtueSessionComponent, VR : VirtueRoute> virtueApplication(
  appComponentFactory: (VirtuePlatformComponent, WasmVirtueConfig) -> A,
  sessionComponentFactory: (A, VirtuePlatformSessionComponent) -> S,
  config: WasmVirtueConfig,
  sessionParams: VirtueSession.Params<S, VR>,
  defaultThemeSetting: ThemeSetting = ThemeSetting.System,
  initialRouteProvider: (Uri) -> VR = { sessionParams.initialRoute },
) {
  val webPlatformComponent = VirtueWebPlatformComponent.create()

  val virtuePlatformComponent: VirtuePlatformComponent =
    VirtuePlatformComponent.create(
      webComponent = webPlatformComponent,
    )

  val virtuePlatformSessionComponent = VirtuePlatformSessionComponent.create()

  val appComponent = appComponentFactory(virtuePlatformComponent, config)
  appComponent.initializer.initialize()

  val sessionComponent = sessionComponentFactory(
    appComponent,
    virtuePlatformSessionComponent,
  )

  val initialRoute = initialRouteProvider(Uri.parse(webPlatformComponent.browserLocation.href))

  CanvasBasedWindow(config.title) {
    InitializationEffect(
      themeSettings = appComponent.themeSettings,
      defaultThemeSetting = defaultThemeSetting,
    )

    WithBackPressDispatching(
      onBackPressedDispatcher = (sessionComponent as OnBackPressDispatcherProvider).onBackPressedDispatcher,
    ) {
      sessionComponent.session.SessionUi(
        sessionComponent = sessionComponent,
        params = sessionParams.copy(
          initialRoute = initialRoute,
        ),
      )
    }
  }
}
