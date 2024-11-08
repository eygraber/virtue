package com.eygraber.virtue.app.compat

import android.content.Intent
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import com.eygraber.uri.Uri
import com.eygraber.virtue.app.VirtueActivityDelegate
import com.eygraber.virtue.di.components.VirtueAppComponent
import com.eygraber.virtue.di.components.VirtuePlatformSessionComponent
import com.eygraber.virtue.session.VirtueSession
import com.eygraber.virtue.session.VirtueSessionComponent
import com.eygraber.virtue.session.nav.VirtueDeepLink
import com.eygraber.virtue.session.nav.VirtueRoute

public abstract class VirtueAppCompatActivity<A, S, VR> : AppCompatActivity()
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
    get() =
      delegate.appComponent

  protected val virtuePlatformSessionComponent: VirtuePlatformSessionComponent
    get() =
      delegate.virtuePlatformSessionComponent

  protected val sessionComponent: S
    get() =
      delegate.sessionComponent

  protected abstract fun createSessionComponent(
    appComponent: A,
    virtuePlatformSessionComponent: VirtuePlatformSessionComponent,
  ): S

  protected abstract val sessionParams: VirtueSession.Params<S, VR>

  protected open fun createDeepLink(
    deepLink: Uri,
  ): VirtueDeepLink<VR>? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    delegate.onCreate(
      savedInstanceState = savedInstanceState,
      isEdgeToEdge = isEdgeToEdge,
      sessionUiWrapper = { _, _, content -> content() },
    ) {
      super.onCreate(savedInstanceState)
    }
  }

  @Composable
  protected open fun SessionUiWrapper(
    appComponent: A,
    isApplicationInDarkTheme: Boolean,
    content: @Composable () -> Unit,
  ) {
    content()
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
