package com.eygraber.virtue.session

import com.eygraber.virtue.back.press.dispatch.OnBackPressDispatcherProvider
import com.eygraber.virtue.di.components.VirtueAppComponent
import com.eygraber.virtue.di.components.VirtuePlatformSessionComponent
import com.eygraber.virtue.di.scopes.SessionSingleton
import com.eygraber.virtue.session.state.VirtueSessionStateManager
import me.tatarka.inject.annotations.Provides

public typealias GenericVirtueSessionComponent = VirtueSessionComponent<*>

@SessionSingleton
public interface VirtueSessionComponent<T : VirtueDeepLinkMapper> : OnBackPressDispatcherProvider {
  public val appComponent: VirtueAppComponent

  public val virtuePlatformSessionComponent: VirtuePlatformSessionComponent

  public val session: VirtueSession
  public val sessionManager: VirtueSessionManager
  public val stateManager: VirtueSessionStateManager

  public val deepLinkMapper: VirtueDeepLinkMapper

  @Provides public fun T.providesDeepLinkMapper(): VirtueDeepLinkMapper
}
