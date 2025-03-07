package com.eygraber.virtue.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ComposeUIViewController
import com.eygraber.virtue.back.press.dispatch.OnBackPressDispatcherProvider
import com.eygraber.virtue.back.press.dispatch.WithBackPressDispatching
import com.eygraber.virtue.config.IosVirtueConfig
import com.eygraber.virtue.di.components.VirtueAppComponent
import com.eygraber.virtue.di.components.VirtuePlatformComponent
import com.eygraber.virtue.di.components.VirtuePlatformSessionComponent
import com.eygraber.virtue.di.components.createKmp
import com.eygraber.virtue.session.VirtueSession
import com.eygraber.virtue.session.VirtueSessionComponent
import com.eygraber.virtue.session.nav.VirtueRoute
import com.eygraber.virtue.theme.ThemeSetting
import com.eygraber.virtue.theme.compose.isApplicationInDarkTheme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import platform.UIKit.UIViewController

public abstract class VirtueApplication<A : VirtueAppComponent, S : VirtueSessionComponent, VR : VirtueRoute>(
  private val config: IosVirtueConfig,
  private val defaultThemeSetting: ThemeSetting = ThemeSetting.System,
) {
  private val virtuePlatformComponent = VirtuePlatformComponent.createKmp()

  protected val appComponent: A by lazy(LazyThreadSafetyMode.NONE) {
    createAppComponent(virtuePlatformComponent, config)
  }

  protected abstract fun createAppComponent(
    virtuePlatformComponent: VirtuePlatformComponent,
    config: IosVirtueConfig,
  ): A

  protected val virtuePlatformSessionComponent: VirtuePlatformSessionComponent by lazy(LazyThreadSafetyMode.NONE) {
    VirtuePlatformSessionComponent.createKmp()
  }

  protected val sessionComponent: S by lazy(LazyThreadSafetyMode.NONE) {
    createSessionComponent(
      appComponent = appComponent,
      virtuePlatformSessionComponent = virtuePlatformSessionComponent,
    )
  }

  protected abstract fun createSessionComponent(
    appComponent: A,
    virtuePlatformSessionComponent: VirtuePlatformSessionComponent,
  ): S

  protected abstract val sessionParams: VirtueSession.Params<S, VR>

  public abstract fun createViewController(): UIViewController

  init {
    @OptIn(DelicateCoroutinesApi::class)
    GlobalScope.launch {
      appComponent.themeSettings.initialize(
        default = defaultThemeSetting,
      )
    }

    appComponent.initializer.initialize()
  }

  @Composable
  protected open fun SessionUiWrapper(
    appComponent: A,
    isApplicationInDarkTheme: Boolean,
    content: @Composable () -> Unit,
  ) {
    content()
  }

  protected fun createVirtueViewController(): UIViewController = ComposeUIViewController {
    WithBackPressDispatching(
      onBackPressedDispatcher = (sessionComponent as OnBackPressDispatcherProvider).onBackPressedDispatcher,
    ) {
      val isApplicationInDarkTheme = appComponent.themeSettings.isApplicationInDarkTheme()
      SessionUiWrapper(appComponent, isApplicationInDarkTheme) {
        sessionComponent.session.SessionUi(
          sessionComponent = sessionComponent,
          params = sessionParams,
          isApplicationInDarkTheme = isApplicationInDarkTheme,
        )
      }
    }
  }
}
