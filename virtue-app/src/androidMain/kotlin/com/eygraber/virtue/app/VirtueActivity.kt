package com.eygraber.virtue.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.annotation.CallSuper
import com.eygraber.uri.Uri
import com.eygraber.virtue.di.components.VirtueAppComponent
import com.eygraber.virtue.di.components.VirtuePlatformSessionComponent
import com.eygraber.virtue.session.VirtueSession
import com.eygraber.virtue.session.VirtueSessionComponent
import com.eygraber.virtue.session.nav.VirtueDeepLink
import com.eygraber.virtue.session.nav.VirtueRoute

public abstract class VirtueActivity<A, S, VR> : ComponentActivity()
  where A : VirtueAppComponent, S : VirtueSessionComponent, VR : VirtueRoute {
  private val delegate by lazy(LazyThreadSafetyMode.NONE) {
    VirtueActivityDelegate(
      activity = this,
      createSessionComponent = ::createSessionComponent,
      sessionParams = ::sessionParams,
      createDeepLink = ::createDeepLink,
    )
  }

  protected open val isEdgeToEdge: Boolean = true

  protected val appComponent: A
    get() = delegate.appComponent

  protected val virtuePlatformSessionComponent: VirtuePlatformSessionComponent
    get() = delegate.virtuePlatformSessionComponent

  protected val sessionComponent: S
    get() = delegate.sessionComponent

  protected abstract fun createSessionComponent(
    appComponent: A,
    virtuePlatformSessionComponent: VirtuePlatformSessionComponent,
  ): S

  protected abstract val sessionParams: VirtueSession.Params<S, VR>

  protected open fun createDeepLink(
    deepLink: Uri,
  ): VirtueDeepLink<VR>? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    delegate.onCreate(savedInstanceState, isEdgeToEdge) {
      super.onCreate(savedInstanceState)
    }
  }

  @CallSuper
  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)

    delegate.onNewIntent(intent)
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)

    delegate.onSaveInstanceState(outState)
  }
}
