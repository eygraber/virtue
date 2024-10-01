package com.eygraber.virtue.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowDecoration
import androidx.compose.ui.window.application
import com.eygraber.virtue.back.press.dispatch.OnBackPressDispatcherProvider
import com.eygraber.virtue.back.press.dispatch.WithBackPressDispatching
import com.eygraber.virtue.config.DesktopVirtueConfig
import com.eygraber.virtue.di.components.VirtueAppComponent
import com.eygraber.virtue.di.components.VirtuePlatformComponent
import com.eygraber.virtue.di.components.VirtuePlatformSessionComponent
import com.eygraber.virtue.di.components.create
import com.eygraber.virtue.session.VirtueSession
import com.eygraber.virtue.session.VirtueSessionComponent
import com.eygraber.virtue.session.VirtueSessionParams
import com.eygraber.virtue.session.nav.VirtueRoute
import com.eygraber.virtue.theme.ThemeSetting
import java.awt.Dimension

@OptIn(ExperimentalComposeUiApi::class)
public fun <A : VirtueAppComponent, S : VirtueSessionComponent, VR : VirtueRoute> virtueApplication(
  appComponentFactory: (VirtuePlatformComponent, DesktopVirtueConfig) -> A,
  initialSessionComponentFactory: (A, VirtuePlatformSessionComponent) -> S,
  config: DesktopVirtueConfig,
  sessionParams: VirtueSession.Params<S, VR>,
  configureInitialSessionParams: (VirtueSessionParams<VR>, S) -> VirtueSessionParams<VR> = { params, _ -> params },
  onAllSessionsClosed: ApplicationScope.() -> Unit = { exitApplication() },
  defaultThemeSetting: ThemeSetting = ThemeSetting.System,
) {
  val virtuePlatformComponent: VirtuePlatformComponent =
    VirtuePlatformComponent.create()

  val virtuePlatformSessionComponent = VirtuePlatformSessionComponent.create()

  val appComponent = appComponentFactory(virtuePlatformComponent, config)
  appComponent.initializer.initialize()

  val initialSessionComponent = initialSessionComponentFactory(
    appComponent,
    virtuePlatformSessionComponent,
  )

  val initialSessionParams = configureInitialSessionParams(
    VirtueSessionParams(
      title = config.appInfo.name,
    ),
    initialSessionComponent,
  )

  val sessionManager = initialSessionComponent.sessionManager.apply {
    addSession(initialSessionComponent, initialSessionParams)
  }

  application {
    InitializationEffect(
      themeSettings = appComponent.themeSettings,
      defaultThemeSetting = defaultThemeSetting,
    )

    val sessions by sessionManager.sessions.collectAsState()
    for(sessionEntry in sessions) {
      val sessionComponent = sessionEntry.sessionComponent
      val params = sessionEntry.params
      key(sessionComponent) {
        Window(
          onCloseRequest = {
            if(sessions.size == 1) {
              onAllSessionsClosed()
            }
            else {
              sessionManager.removeSession(sessionComponent)
            }
          },
          state = params.windowState,
          visible = params.visible,
          title = params.title,
          icon = params.icon,
          decoration = when {
            params.undecorated -> WindowDecoration.Undecorated()
            else -> WindowDecoration.SystemDefault
          },
          transparent = params.transparent,
          resizable = params.resizable,
          enabled = params.enabled,
          focusable = params.focusable,
          alwaysOnTop = params.alwaysOnTop,
          onPreviewKeyEvent = params.onPreviewKeyEvent,
          onKeyEvent = params.onKeyEvent,
        ) {
          WindowMinSizeEffect(
            params = params,
            window = window,
          )

          WithBackPressDispatching(
            onBackPressedDispatcher = (sessionComponent as OnBackPressDispatcherProvider).onBackPressedDispatcher,
          ) {
            @Suppress("UNCHECKED_CAST")
            sessionComponent.session.SessionUi(
              sessionComponent = sessionComponent as S,
              params = sessionParams.copy(
                initialRoute = params.initialRoute as? VR ?: sessionParams.initialRoute,
              ),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun WindowMinSizeEffect(
  window: ComposeWindow,
  params: VirtueSessionParams<*>,
) {
  val minimumWindowSize = when(val minSize = params.minWindowSize) {
    null -> null
    else -> with(LocalDensity.current) {
      remember(density, minSize) { Dimension(minSize.width.roundToPx(), minSize.height.roundToPx()) }
    }
  }

  DisposableEffect(window, minimumWindowSize) {
    if(minimumWindowSize != null) {
      window.minimumSize = minimumWindowSize
    }

    onDispose {}
  }
}
