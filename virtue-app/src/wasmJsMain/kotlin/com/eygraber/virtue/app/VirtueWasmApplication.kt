package com.eygraber.virtue.app

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.eygraber.uri.Url
import com.eygraber.virtue.back.press.dispatch.OnBackPressDispatcherProvider
import com.eygraber.virtue.back.press.dispatch.WithBackPressDispatching
import com.eygraber.virtue.config.WasmVirtueConfig
import com.eygraber.virtue.di.components.VirtueAppComponent
import com.eygraber.virtue.di.components.VirtuePlatformComponent
import com.eygraber.virtue.di.components.VirtuePlatformSessionComponent
import com.eygraber.virtue.di.components.VirtueWebPlatformComponent
import com.eygraber.virtue.di.components.create
import com.eygraber.virtue.session.GenericVirtueSessionComponent
import com.eygraber.virtue.session.VirtueSession
import com.eygraber.virtue.theme.ThemeSetting

@OptIn(ExperimentalComposeUiApi::class)
public fun <A : VirtueAppComponent, S : GenericVirtueSessionComponent> virtueApplication(
  appComponentFactory: (VirtuePlatformComponent, WasmVirtueConfig) -> A,
  sessionComponentFactory: (A, VirtuePlatformSessionComponent) -> S,
  config: WasmVirtueConfig,
  sessionParams: VirtueSession.Params<S>,
  defaultThemeSetting: ThemeSetting = ThemeSetting.System,
) {
  val webPlatformComponent = VirtueWebPlatformComponent.create()

  val virtuePlatformComponent: VirtuePlatformComponent =
    VirtuePlatformComponent.create(
      webComponent = webPlatformComponent,
    )

  val virtuePlatformSessionComponent = VirtuePlatformSessionComponent.create()

  val appComponent = appComponentFactory(virtuePlatformComponent, config)

  val sessionComponent = sessionComponentFactory(
    appComponent,
    virtuePlatformSessionComponent,
  )

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
          startDestination = sessionComponent.deepLinkMapper.mapToRoute(
            Url.parse(webPlatformComponent.browserLocation.href),
          ),
        ),
      )
    }
  }
}
