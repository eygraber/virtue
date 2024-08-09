package com.eygraber.virtue.app

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.eygraber.uri.Uri
import com.eygraber.virtue.back.press.dispatch.OnBackPressDispatcherProvider
import com.eygraber.virtue.back.press.dispatch.WithBackPressDispatching
import com.eygraber.virtue.config.JsVirtueConfig
import com.eygraber.virtue.di.components.VirtueAppComponent
import com.eygraber.virtue.di.components.VirtuePlatformComponent
import com.eygraber.virtue.di.components.VirtuePlatformSessionComponent
import com.eygraber.virtue.di.components.VirtueWebPlatformComponent
import com.eygraber.virtue.di.components.create
import com.eygraber.virtue.session.VirtueSession
import com.eygraber.virtue.session.VirtueSessionComponent
import com.eygraber.virtue.session.nav.VirtueRoute
import com.eygraber.virtue.theme.ThemeSetting
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class)
public fun <A : VirtueAppComponent, S : VirtueSessionComponent, VR : VirtueRoute> virtueApplication(
  appComponentFactory: (VirtuePlatformComponent, JsVirtueConfig) -> A,
  sessionComponentFactory: (A, VirtuePlatformSessionComponent) -> S,
  config: JsVirtueConfig,
  sessionParams: VirtueSession.Params<S, VR>,
  defaultThemeSetting: ThemeSetting = ThemeSetting.System,
  initialRouteProvider: (Uri) -> VR = { sessionParams.initialRoute },
) {
  val webPlatformComponent = VirtueWebPlatformComponent.create()

  val virtuePlatformComponent: VirtuePlatformComponent =
    VirtuePlatformComponent.create(
      webComponent = webPlatformComponent,
    )

  val appComponent = appComponentFactory(virtuePlatformComponent, config)

  val virtuePlatformSessionComponent = VirtuePlatformSessionComponent.create()

  val sessionComponent = sessionComponentFactory(
    appComponent,
    virtuePlatformSessionComponent,
  )

  val initialRoute = initialRouteProvider(Uri.parse(webPlatformComponent.browserLocation.href))

  onWasmReady {
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
}
