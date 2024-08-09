package com.eygraber.virtue.session

import com.eygraber.virtue.back.press.dispatch.OnBackPressDispatcherProvider
import com.eygraber.virtue.di.components.VirtueAppComponent
import com.eygraber.virtue.di.components.VirtuePlatformSessionComponent
import com.eygraber.virtue.di.scopes.SessionSingleton
import com.eygraber.virtue.session.state.VirtueSessionStateManager

@SessionSingleton
public interface VirtueSessionComponent : OnBackPressDispatcherProvider {
  public val appComponent: VirtueAppComponent

  public val virtuePlatformSessionComponent: VirtuePlatformSessionComponent

  public val session: VirtueSession
  public val sessionManager: VirtueSessionManager
  public val stateManager: VirtueSessionStateManager
}
