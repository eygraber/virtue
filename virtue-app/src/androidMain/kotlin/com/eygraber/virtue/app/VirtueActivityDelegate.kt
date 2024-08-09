package com.eygraber.virtue.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.lifecycleScope
import com.eygraber.uri.Uri
import com.eygraber.uri.toUri
import com.eygraber.virtue.di.components.VirtueAppComponent
import com.eygraber.virtue.di.components.VirtuePlatformSessionComponent
import com.eygraber.virtue.di.components.create
import com.eygraber.virtue.session.VirtueSession
import com.eygraber.virtue.session.VirtueSessionComponent
import com.eygraber.virtue.session.nav.VirtueDeepLink
import com.eygraber.virtue.session.nav.VirtueRoute
import com.eygraber.virtue.theme.compose.isApplicationInDarkTheme
import kotlinx.coroutines.launch

public class VirtueActivityDelegate<A, S, VR>(
  private val activity: ComponentActivity,
  private val createSessionComponent: (A, VirtuePlatformSessionComponent) -> S,
  private val sessionParams: () -> VirtueSession.Params<S, VR>,
  private val createDeepLink: (Uri) -> VirtueDeepLink<VR>?,
) where A : VirtueAppComponent, S : VirtueSessionComponent, VR : VirtueRoute {
  private val virtueApp: VirtueApplication<A> by lazy(LazyThreadSafetyMode.NONE) {
    requireNotNull(
      runCatching {
        @Suppress("UNCHECKED_CAST")
        activity.applicationContext as? VirtueApplication<A>
      }.getOrNull(),
    ) {
      "applicationContext must be a VirtueApplication typed to the same A as VirtueActivity"
    }
  }

  public val virtuePlatformSessionComponent: VirtuePlatformSessionComponent by lazy(LazyThreadSafetyMode.NONE) {
    VirtuePlatformSessionComponent.create(
      context = activity,
    )
  }

  public val appComponent: A by lazy(LazyThreadSafetyMode.NONE) {
    virtueApp.appComponent
  }

  public val sessionComponent: S by lazy(LazyThreadSafetyMode.NONE) {
    createSessionComponent(appComponent, virtuePlatformSessionComponent)
  }

  public fun onCreate(savedInstanceState: Bundle?, isEdgeToEdge: Boolean, callSuper: () -> Unit) {
    savedInstanceState?.let(sessionComponent.stateManager::onRestoreState)

    if(isEdgeToEdge) {
      activity.enableEdgeToEdge()
    }

    callSuper()

    val initialRoute = activity.intent?.data?.toUri()?.let(createDeepLink)?.route ?: sessionParams().initialRoute

    activity.setContent {
      if(isEdgeToEdge) {
        activity.SystemUiController(
          isDarkTheme = appComponent.themeSettings.isApplicationInDarkTheme(),
        )
      }

      sessionComponent.session.SessionUi(
        sessionComponent = sessionComponent,
        params = sessionParams().copy(
          initialRoute = initialRoute,
        ),
      )
    }
  }

  public fun onNewIntent(intent: Intent) {
    intent.data?.let(::handleDeepLink)
  }

  private fun handleDeepLink(deepLinkUri: android.net.Uri) {
    createDeepLink(deepLinkUri.toUri())?.let { deepLink ->
      activity.lifecycleScope.launch {
        sessionParams().deepLinksFlow.emit(deepLink)
      }
    }
  }

  public fun onSaveInstanceState(outState: Bundle) {
    sessionComponent.stateManager.onSaveState(outState)
  }
}

@Composable
private fun ComponentActivity.SystemUiController(
  isDarkTheme: Boolean,
) {
  DisposableEffect(isDarkTheme) {
    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.auto(
        Color.TRANSPARENT,
        Color.TRANSPARENT,
      ) { isDarkTheme },
      navigationBarStyle = SystemBarStyle.auto(
        lightScrim,
        darkScrim,
      ) { isDarkTheme },
    )

    onDispose {}
  }
}

/**
 * The default light scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * The default dark scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)
